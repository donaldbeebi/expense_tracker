package edu.cuhk.expensetracker

import androidx.appcompat.app.AppCompatActivity
import edu.cuhk.expensetracker.game.SharedPreferencesManager
import android.os.Bundle
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.Moshi
import java.io.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.squareup.moshi.adapter
import edu.cuhk.expensetracker.room.DB
import edu.cuhk.expensetracker.room.expenseentry.ExpenseEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    //private lateinit var confirmButton: Button
    private lateinit var amountEditText: EditText
    private lateinit var titleEditText: EditText
    private var categorySelected: Boolean = false
    private lateinit var categoryMenu: PopupMenu

    //by donald
    private lateinit var mSPM: SharedPreferencesManager
    private lateinit var categoryTextView: TextView

    @OptIn(ExperimentalStdlibApi::class)
    private val jsonAdapter = Moshi.Builder().build().adapter<List<ExpenseEntry>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val input = intent.getStringExtra("input")
        val amount = intent.getFloatExtra("PresetAmount", Float.NaN)
        val category = intent.getStringExtra("PresetCategory")

        findViewById<Button>(R.id.confirm_button).apply {
            setOnClickListener { confirmEntry() }
        }
        titleEditText = findViewById<EditText>(R.id.input_title_input).apply {
            if (input != null) setText(input)
        }
        amountEditText = findViewById<EditText>(R.id.input_amount_input).apply {
            if (!amount.isNaN()) setText(amount.toString())
        }
        categoryTextView = findViewById<TextView>(R.id.input_category_input).apply {
            if (category != null) {
                categorySelected = true
                text = category
                textSize = 24f
                setTextColor(Color.parseColor("#3F3F3F"))
            }
        }
        categoryMenu = PopupMenu(this, categoryTextView).apply {
            setOnMenuItemClickListener(this@InputActivity)
            inflate(R.menu.category_menu)
        }
        //by donald
        mSPM = SharedPreferencesManager.getInstance()
    }

    private fun confirmEntry() {
        val title = titleEditText.text.toString()
        val amountString = amountEditText.text.toString()
        val category = categoryTextView.text.toString()
        if (!categorySelected || title == "" || amountString == "") {
            Toast.makeText(this, "Please fill in all the required fields!", Toast.LENGTH_LONG).show()
            return
        }
        val amount = amountString.toFloatOrNull()
            ?: return Toast.makeText(this, "$amountString is not a valid amount!", Toast.LENGTH_LONG).show()

        val dateString = run {
            val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val saveRecordDate = LocalDate.now(ZoneId.of("Asia/Hong_Kong"))
            dtf.format(saveRecordDate)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            DB.expenseEntryDao().insert(
                ExpenseEntry(
                    title = title,
                    amount = amount,
                    category = category,
                    date = dateString
                )
            )
        }

        //the user earns 1 buck when he finishes logging
        val earnedBucks = 1
        mSPM.addBucks(earnedBucks)
        Toast.makeText(this, "Logging complete. You just earned $earnedBucks buck.", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun showCategory(view: View) {
        categoryMenu.show()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.rootView.applicationWindowToken, 0)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        categoryTextView.text = item.title.toString()
        categoryTextView.setTextColor(Color.parseColor("#3F3F3F"))
        categoryTextView.textSize = 24f
        categorySelected = true
        return true
    }

    // this event will enable the back function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}