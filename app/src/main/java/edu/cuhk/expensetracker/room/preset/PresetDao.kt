package edu.cuhk.expensetracker.room.preset

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import edu.cuhk.expensetracker.room.preset.Preset

@Dao
interface PresetDao {
    @Query("SELECT * FROM preset")
    suspend fun getAll(): List<Preset>

    @Insert
    suspend fun insert(preset: Preset)

    @Delete
    suspend fun delete(preset: Preset)

    @Query("DELETE FROM preset")
    suspend fun debug_deleteAll()
}