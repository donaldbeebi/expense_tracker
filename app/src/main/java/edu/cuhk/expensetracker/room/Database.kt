package edu.cuhk.expensetracker.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.cuhk.expensetracker.room.expenseentry.ExpenseEntry
import edu.cuhk.expensetracker.room.expenseentry.ExpenseEntryDao
import edu.cuhk.expensetracker.room.preset.Preset
import edu.cuhk.expensetracker.room.preset.PresetDao

@Database(entities = [Preset::class, ExpenseEntry::class], version = 1)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
    abstract fun expenseEntryDao(): ExpenseEntryDao
}

lateinit var DB: ExpenseDatabase
    private set

fun initDB(context: Context) {
    DB = Room.databaseBuilder(
        context,
        ExpenseDatabase::class.java,
        "database"
    ).build()
}