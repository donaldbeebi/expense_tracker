package edu.cuhk.expensetracker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.lifecycleScope
import edu.cuhk.expensetracker.room.DB
import edu.cuhk.expensetracker.room.preset.Preset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.nio.charset.Charset

class AddPresetActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    private lateinit var titleEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var categoryTextView: TextView
    private lateinit var categoryMenu: PopupMenu

    private val FILE_PATH = "/data/data/edu.cuhk.expensetracker/files/preset"
    private val FILE_PATH_dup = "/data/data/edu.cuhk.expensetracker/files/preset_dup"
    private val RECORD_FILE_PATH = "/data/data/edu.cuhk.expensetracker/files/record"
    private val RECORD_FILE_PATH_dup = "/data/data/edu.cuhk.expensetracker/files/record_dup"
    private var categorySelected = false
    //private val category: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_preset)

        // showing the back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleEditText = findViewById(R.id.add_preset_title_input)
        amountEditText = findViewById(R.id.add_preset_amount_input)
        categoryTextView = findViewById(R.id.add_preset_category_input)

        categoryMenu = PopupMenu(this, categoryTextView).apply {
            setOnMenuItemClickListener(this@AddPresetActivity)
            inflate(R.menu.category_menu)
        }

        //setting save preset button
        findViewById<Button>(R.id.SavePresetButton).apply {
            setOnClickListener { savePreset() }
            /*if (category != null) {
                when (category) {
                    "Food" -> {
                        categoryTextView.setText("Food")
                        return
                    }
                    "Daily Necessities" -> {
                        categoryTextView.setText("Daily Necessities")
                        return
                    }
                    "Transportation" -> {
                        categoryTextView.setText("Transportation")
                        return
                    }
                    "Clothes" -> {
                        categoryTextView.setText("Clothes")
                        return
                    }
                    "Entertainment" -> {
                        categoryTextView.setText("Entertainment")
                        return
                    }
                    "Transfer Fee" -> {
                        categoryTextView.setText("Transfer Fee")
                        return
                    }
                    "Health" -> {
                        categoryTextView.setText("Health")
                        return
                    }
                    "Beauty" -> {
                        categoryTextView.setText("Beauty")
                        return
                    }
                    "Utilities" -> {
                        categoryTextView.setText("Utilities")
                        return
                    }
                    "Others" -> {
                        categoryTextView.setText("Others")
                        return
                    }
                }
            }
            categorySelected = 0*/
        }
    }

    fun showCategory(view: View) {
        categoryMenu.show()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.rootView.applicationWindowToken, 0)
    }

    // this event will enable the back function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        categoryTextView.text = item.title.toString()
        categoryTextView.setTextColor(Color.parseColor("#3F3F3F"))
        categoryTextView.textSize = 24f
        categorySelected = true
        return true
    }

    // called when the save as preset button is clicked
    private fun savePreset() {
        val title = titleEditText.text.toString()
        val amountString = amountEditText.text.toString()
        val category = categoryTextView.text.toString()

        Log.d("AddPresetActivity", category)
        if (title == "" || amountString == "" || !categorySelected) {
            Toast.makeText(this, "Please fill in all the required fields!", Toast.LENGTH_LONG).show()
            return
        }

        val amount = amountString.toFloatOrNull()
            ?: return run { Toast.makeText(this, "${amountEditText.text} is not a valid amount!", Toast.LENGTH_LONG).show() }

        lifecycleScope.launch(Dispatchers.IO) {
            val preset = Preset(
                title = title,
                amount = amount,
                category = category
            )
            DB.presetDao().insert(preset)
        }
        /*var `is`: FileInputStream? = null
        var isd: FileInputStream? = null
        try {
            isd = FileInputStream(FILE_PATH_dup)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val br = BufferedReader(InputStreamReader(isd, Charset.forName("UTF-8")))
        var fos: FileOutputStream? = null
        var line = ""
        try {
            val TARGET = "$title,$savePresetAmount,$category\n"
            Toast.makeText(this, "Saved preset", Toast.LENGTH_SHORT).show()
            fos = openFileOutput("preset", MODE_PRIVATE)
            fos.write(TARGET.toByteArray())
            while (br.readLine().also { line = it } != null) {
                val output = """
                    $line
                    
                    """.trimIndent()
                fos.write(output.toByteArray())
                //Toast.makeText(this, "overwriting", Toast.LENGTH_SHORT).show();
            }
        } catch (fileNotFoundException: FileNotFoundException) {
            fileNotFoundException.printStackTrace()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        //overwrite duplicate file
        try {
            `is` = FileInputStream(FILE_PATH)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val brd = BufferedReader(InputStreamReader(`is`, Charset.forName("UTF-8")))
        var fosd: FileOutputStream? = null
        try {
            fosd = openFileOutput("preset_dup", MODE_PRIVATE)
            while (brd.readLine().also { line = it } != null) {
                val output = """
                    $line
                    
                    """.trimIndent()
                fosd.write(output.toByteArray())
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
        setResult(RESULT_OK)
        finish()
    }
}