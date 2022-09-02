package edu.cuhk.expensetracker.room.expenseentry

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ExpenseEntry::class], version = 1)
abstract class ExpenseEntryDatabase : RoomDatabase() {
    abstract fun expenseEntryDao(): ExpenseEntryDao
}