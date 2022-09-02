package edu.cuhk.expensetracker

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import edu.cuhk.expensetracker.RecordListAdapter.RecordViewHolder
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import edu.cuhk.expensetracker.room.expenseentry.ExpenseEntry

@Deprecated("No longer needed")
class RecordListAdapter(
    context: Context,
    private val entries: List<ExpenseEntry>
) : RecyclerView.Adapter<RecordViewHolder>() {
    //var mRecordItem: Array<String>
    //var mRecordAmount: Array<String>
    //var mRecordCategory: Array<String>
    //var mRecordDate: Array<String>
    private val inflater = LayoutInflater.from(context)

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.recordTitle)
        var amountTextView: TextView = itemView.findViewById(R.id.recordDate)
        var dateTextView: TextView = itemView.findViewById(R.id.recordAmount)
        var categoryTextView: TextView = itemView.findViewById(R.id.recordCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView = inflater.inflate(R.layout.recordlist_item, parent, false)
        return RecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        // Update the following to display correct information based on the given position

        // Set up View items for this row (position), modify to show correct information read from the CSV
        with(entries[position]) {
            holder.titleTextView.text = title
            holder.dateTextView.text = date
            holder.categoryTextView.text = category
            holder.amountTextView.text = "$%.1f".format(amount)
        }
    }

    override fun getItemCount(): Int {
        return entries.size
    }
}