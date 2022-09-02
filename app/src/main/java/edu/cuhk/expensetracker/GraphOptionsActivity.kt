package edu.cuhk.expensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import edu.cuhk.expensetracker.room.DB
import edu.cuhk.expensetracker.room.expenseentry.ExpenseEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*

@Deprecated("No longer needed")
class GraphOptionsActivity : AppCompatActivity() {
    private lateinit var buttonCategory: Button
    private lateinit var buttonTime: Button
    private lateinit var buttonType: Button
    private var category: String? = null
    private var time: String? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_options)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // get records from the OverviewActivity
        buttonCategory = findViewById(R.id.button_category)
        buttonTime = findViewById(R.id.button_time)
        buttonType = findViewById(R.id.button_type)

        findViewById<Button>(R.id.button_view_chart).setOnClickListener { generateGraph() }
    }

    // this event will enable the back function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // create the category pop up menu
    @SuppressLint("NonConstantResourceId", "SetTextI18n")
    fun showPopupCategory(v: View?) {
        @SuppressLint("RtlHardcoded")
        val popup = PopupMenu(this, v, Gravity.RIGHT)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.category_graph_menu, popup.menu)
        popup.show()

        // the on menu item click behaviour of the category pop up menu
        popup.setOnMenuItemClickListener { item: MenuItem ->
            category = item.title.toString()
            buttonCategory.text = category
            true
        }
    }

    // create the time pop up menu
    @SuppressLint("NonConstantResourceId", "SetTextI18n")
    fun showPopupTime(v: View?) {
        @SuppressLint("RtlHardcoded")
        val popup = PopupMenu(this, v, Gravity.RIGHT)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.time_menu, popup.menu)
        popup.show()

        // the on menu item click behaviour of the category pop up menu
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.time1 -> {
                    time = "By Days"
                    buttonTime.text = time
                    return@setOnMenuItemClickListener true
                }
                R.id.time2 -> {
                    time = "By Months"
                    buttonTime.text = time
                    return@setOnMenuItemClickListener true
                }
                R.id.time3 -> {
                    time = "By Years"
                    buttonTime.text = time
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
    }

    // create the graph type pop up menu
    @SuppressLint("SetTextI18n", "NonConstantResourceId") fun showPopupType(v: View?) {
        @SuppressLint("RtlHardcoded") val popup = PopupMenu(this, v, Gravity.RIGHT)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.type_menu, popup.menu)
        popup.show()

        // the on menu item click behaviour of the category pop up menu
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.type1 -> {
                    type = "Bar Chart"
                    buttonType.text = type
                    return@setOnMenuItemClickListener true
                }
                R.id.type2 -> {
                    type = "Line Chart"
                    buttonType.text = type
                    return@setOnMenuItemClickListener true
                }
                R.id.type3 -> {
                    type = "Pie Chart"
                    buttonType.text = type
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
    }

    private fun generateGraph() {
        if (category != null && time != null && type != null) {
            when (type) {
                "Bar Chart" -> {
                    val newIntent = Intent(this@GraphOptionsActivity, BarChartActivity::class.java)
                    // send category and time to BarChartActivity
                    newIntent.putExtra("category", category)
                    newIntent.putExtra("time", time)
                    startActivity(newIntent)
                }
                "Line Chart" -> {
                    val newIntent = Intent(this@GraphOptionsActivity, LineChartActivity::class.java)
                    // send category and time to LineChartActivity
                    newIntent.putExtra("category", category)
                    newIntent.putExtra("time", time)
                    startActivity(newIntent)
                }
                "Pie Chart" -> {
                    val newIntent = Intent(this@GraphOptionsActivity, PieChartActivity::class.java)
                    // send category and time to LineChartActivity
                    newIntent.putExtra("category", category)
                    newIntent.putExtra("time", time)
                    startActivity(newIntent)
                }
            }
        } else {
            Toast.makeText(this, "Error, please make sure you selected a category, a time and a chart type", Toast.LENGTH_LONG).show()
        }
    }
}