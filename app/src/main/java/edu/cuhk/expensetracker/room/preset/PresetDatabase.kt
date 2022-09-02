package edu.cuhk.expensetracker.room.preset

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.cuhk.expensetracker.room.preset.Preset
import edu.cuhk.expensetracker.room.preset.PresetDao

@Database(entities = [Preset::class], version = 1)
abstract class PresetDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
}