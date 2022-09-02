package edu.cuhk.expensetracker.room.preset

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "preset")
@JsonClass(generateAdapter = true)
data class Preset(
    val title: String,
    val amount: Float,
    val category: String,
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0
)