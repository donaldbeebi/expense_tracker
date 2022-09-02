package edu.cuhk.expensetracker.collections

import edu.cuhk.expensetracker.room.preset.Preset

class PresetList(
    presets: List<Preset>
) {
    private val dataList = presets.map { Data(it, true) }

    val countOfAll: Int = dataList.size

    val countOfExisting: Int
        get() = dataList.count { it.exists }

    fun removeFromAllAt(index: Int): Preset {
        dataList[index].exists = false
        return dataList[index].preset
    }

    fun getFromAll(index: Int): Preset = dataList[index].preset

    fun getFromExisting(index: Int): Preset {
        // TODO: OPTIMIZE?
        val filtered = dataList.filter { it.exists }
        return filtered[index].preset
    }

    private data class Data(val preset: Preset, var exists: Boolean)
}