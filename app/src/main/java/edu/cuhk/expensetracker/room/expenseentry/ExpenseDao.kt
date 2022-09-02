package edu.cuhk.expensetracker.room.expenseentry

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseEntryDao {
    @Query("SELECT * FROM expense_entry")
    suspend fun getAll(): List<ExpenseEntry>

    @Insert
    suspend fun insert(expenseEntry: ExpenseEntry)
}