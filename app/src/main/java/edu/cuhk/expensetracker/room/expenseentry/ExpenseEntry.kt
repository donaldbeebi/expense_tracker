package edu.cuhk.expensetracker.room.expenseentry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "expense_entry")
@JsonClass(generateAdapter = true)
data class ExpenseEntry(
    val title: String,
    val amount: Float,
    val category: String,
    val date: String,
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0
)
