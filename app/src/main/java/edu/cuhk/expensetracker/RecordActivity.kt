package edu.cuhk.expensetracker

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.appcompattheme.AppCompatTheme
import edu.cuhk.expensetracker.room.DB
import edu.cuhk.expensetracker.room.expenseentry.ExpenseEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordActivity : AppCompatActivity() {
    //private lateinit var recyclerView: RecyclerView
    private var entryListState: EntryListState by mutableStateOf(EntryListState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_record)
        setContent {
            AppCompatTheme {
                EntryList(entryListState)
            }
        }

        // showing the back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*recyclerView = findViewById<RecyclerView>(R.id.recyclerview).apply {
            layoutManager = LinearLayoutManager(this@RecordActivity)
        }*/

        loadEntries()
    }

    private fun loadEntries() = lifecycleScope.launch(Dispatchers.IO) {
        val entries = DB.expenseEntryDao().getAll()
        //val adapter = RecordListAdapter(this@RecordActivity, entries)
        entryListState = EntryListState.Ready(entries)
        //recyclerView.adapter = adapter
    }

    // this event will enable the back function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

@Preview(showBackground = true)
@Composable
private fun EntryListPreview() {
    val entries = remember {
        SnapshotStateList<ExpenseEntry>().apply {
            for (i in 0..8) {
                this += ExpenseEntry(
                    title = "Title $i",
                    amount = i * 5F,
                    category = "Category $i",
                    date = "1/1/2022"
                )
            }
        }
    }
    EntryList(EntryListState.Ready(entries))
}

@Composable
private fun EntryList(
    state: EntryListState
) {
    when (state) {
        is EntryListState.Loading -> Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        is EntryListState.Ready -> if (state.entries.isNotEmpty()) LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            item { Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)) }
            items(state.entries.size) { i ->
                EntryItem(state.entries[i])
            }
        } else Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "No expense entries found!",
                fontSize = 26.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
}

@Composable
private fun EntryItem(
    entry: ExpenseEntry
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = entry.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.category,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
                Text(
                    text = entry.date,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray
                )

            }
            Text(
                text = "$%.1f".format(entry.amount),
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(16.dp))
    }

}

private sealed class EntryListState {
    object Loading : EntryListState()
    class Ready(val entries: List<ExpenseEntry>) : EntryListState()
}