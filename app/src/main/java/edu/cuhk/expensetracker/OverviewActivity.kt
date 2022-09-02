package edu.cuhk.expensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.cuhk.expensetracker.R
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import edu.cuhk.expensetracker.RecordActivity
import edu.cuhk.expensetracker.GraphOptionsActivity
import java.util.*

@Deprecated("No longer needed")
class OverviewActivity : AppCompatActivity() {
    private lateinit var buttonRecords: Button
    private lateinit var buttonGraphs: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        // showing the back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonRecords = findViewById<Button>(R.id.view_by_records_button).apply {
            setOnClickListener { // send records to RecordActivity
                val newIntent = Intent(this@OverviewActivity, RecordActivity::class.java)
                startActivity(newIntent)
            }
        }
        buttonGraphs = findViewById<Button>(R.id.view_by_graphs_button).apply {
            setOnClickListener { // send records to GraphOptionsActivity
                val newIntent = Intent(this@OverviewActivity, GraphOptionsActivity::class.java)
                startActivity(newIntent)
            }
        }
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