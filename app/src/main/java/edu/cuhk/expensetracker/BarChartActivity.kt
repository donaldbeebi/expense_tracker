package edu.cuhk.expensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.cuhk.expensetracker.R
import android.content.Intent
import android.graphics.Color
import android.view.MenuItem
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.Legend
import edu.cuhk.expensetracker.room.DB
import edu.cuhk.expensetracker.room.expenseentry.ExpenseEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@Deprecated("No longer used")
class BarChartActivity : AppCompatActivity() {
    private lateinit var entries: List<ExpenseEntry>
    private lateinit var category: String
    private lateinit var time: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*runBlocking(Dispatchers.IO) {
            val entries = DB.expenseEntryDao().getAll()
            withContext(Dispatchers.Main) { this@BarChartActivity.entries = entries }
        }*/
        entries = ArrayList<ExpenseEntry>().apply {
            repeat(3) { i ->
                repeat(5) { j ->
                    this += ExpenseEntry(
                        "Title ${i}_${j}",
                        ((i + j * i) * 100).toFloat(),
                        "Category ${i}_${j}",
                        "%02d".format((i + j * i) + 1) + "/08/2022"
                    )
                }
            }
        }

        // get category and time from the GraphOptionsActivity
        category = intent.getStringExtra("category") ?: throw IllegalStateException("No category is provided")
        time = intent.getStringExtra("time") ?: throw IllegalStateException("No time is provided")

        // creating the bar chart
        val barChart = findViewById<BarChart>(R.id.barChart)

        // get the data
        val spending = data

        // setting different attributes of the bar chart
        val barDataSet = BarDataSet(spending, "Spending")
        barDataSet.colors = obtainColor()
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 16f
        val barData = BarData(barDataSet)

        // setting the x axis of the bar chart
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
        xAxis.granularity = 1f

        // set the position of the legend
        val legend = barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.formSize = 16f
        legend.textSize = 16f
        legend.isWordWrapEnabled = true
        barChart.setFitBars(true)
        barChart.data = barData
        barChart.description.text = ""
    }

    // this event will enable the back function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // check the date of the data to decided whether it should be included in the chart
    // and return its position in the chart
    private fun checkDate(date: String?): Int {
        var position = -1
        when (time) {
            "By Days" -> {
                val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dayDiff = ChronoUnit.DAYS.between(LocalDate.parse(date, dtf), LocalDate.now(ZoneId.of("Asia/Hong_Kong")))
                if (dayDiff < 7) {
                    position = 6 - dayDiff.toInt()
                }
            }
            "By Months" -> {
                val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val monthDiff = LocalDate.now(ZoneId.of("Asia/Hong_Kong")).year * 12 + LocalDate.now(ZoneId.of("Asia/Hong_Kong")).monthValue - LocalDate.parse(date, dtf).year * 12 - LocalDate.parse(date, dtf).monthValue
                if (monthDiff < 6) {
                    position = 5 - monthDiff
                }
            }
            "By Years" -> {
                val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val yearDiff = LocalDate.now(ZoneId.of("Asia/Hong_Kong")).year - LocalDate.parse(date, dtf).year
                if (yearDiff < 5) {
                    position = 4 - yearDiff
                }
            }
        }
        return position
    }

    // set the x-axis value
    private val xAxisValues: ArrayList<String>
        get() {
            val xAxis = ArrayList<String>()
            when (time) {
                "By Days" -> {
                    var i = 6
                    while (i >= 0) {
                        xAxis.add(LocalDate.now(ZoneId.of("Asia/Hong_Kong")).minusDays(i.toLong()).format(DateTimeFormatter.ofPattern("MMM dd")))
                        i--
                    }
                }
                "By Months" -> {
                    var i = 5
                    while (i >= 0) {
                        xAxis.add(LocalDate.now(ZoneId.of("Asia/Hong_Kong")).minusMonths(i.toLong()).format(DateTimeFormatter.ofPattern("MMM/yyyy")))
                        i--
                    }
                }
                "By Years" -> {
                    var i = 4
                    while (i >= 0) {
                        xAxis.add(LocalDate.now(ZoneId.of("Asia/Hong_Kong")).minusYears(i.toLong()).format(DateTimeFormatter.ofPattern("yyyy")))
                        i--
                    }
                }
            }
            return xAxis
        }

    // obtain the data
    // check the date of the data, add the data to the amountList if it is within the time range selected by users
    private val data: ArrayList<BarEntry>
        get() {
            val spending = ArrayList<BarEntry>()
            var position = 0
            val amountList: FloatArray
            when (time) {
                "By Days" -> {
                    amountList = FloatArray(7)
                    run {
                        var i = 0
                        while (i < entries.size) {
                            if (entries[i].category == category || category == "All") {
                                position = checkDate(entries[i].date)
                                if (position != -1) {
                                    amountList[position] += entries[i].amount
                                }
                            }
                            i++
                        }
                    }
                    var i = 0
                    while (i < 7) {
                        // TODO: CHECK IF i.toFloat() IS CORRECT
                        spending.add(BarEntry(i.toFloat(), amountList[i]))
                        i++
                    }
                }
                "By Months" -> {
                    amountList = FloatArray(6)
                    run {
                        var i = 0
                        while (i < entries.size) {
                            if (entries[i].category == category || category == "All") {
                                position = checkDate(entries[i].date)
                                if (position != -1) {
                                    amountList[position] += entries[i].amount
                                }
                            }
                            i++
                        }
                    }
                    var i = 0
                    while (i < 6) {
                        spending.add(BarEntry(i.toFloat(), amountList[i]))
                        i++
                    }
                }
                "By Years" -> {
                    amountList = FloatArray(5)
                    run {
                        var i = 0
                        while (i < entries.size) {
                            if (entries[i].category == category || category == "All") {
                                position = checkDate(entries[i].date)
                                if (position != -1) {
                                    amountList[position] += entries[i].amount
                                }
                            }
                            i++
                        }
                    }
                    var i = 0
                    while (i < 5) {
                        spending.add(BarEntry(i.toFloat(), amountList[i]))
                        i++
                    }
                }
            }
            return spending
        }

    // return a color a scheme for the chart
    private fun obtainColor(): ArrayList<Int> {
        val colors = ArrayList<Int>()
        colors.add(Color.rgb(227, 65, 50))
        colors.add(Color.rgb(236, 219, 83))
        colors.add(Color.rgb(191, 216, 51))
        colors.add(Color.rgb(108, 160, 220))
        colors.add(Color.rgb(100, 83, 148))
        if (time == "By Months" || time == "By Days") {
            colors.add(Color.rgb(108, 79, 60))
        }
        if (time == "By Days") {
            colors.add(Color.rgb(179, 182, 185))
        }
        return colors
    }
}