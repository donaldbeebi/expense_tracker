package edu.cuhk.expensetracker

import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import edu.cuhk.expensetracker.room.preset.Preset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [PresetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Deprecated("No longer needed")
class PresetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    //this only used to instantiate the adapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PresetListAdapter
    private lateinit var emptyPresetMessage: TextView

    @OptIn(ExperimentalStdlibApi::class)
    private val jsonAdapter = Moshi.Builder().build().adapter<List<Preset>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_preset, container, false)
        val presets = loadPresetsFromJson()

        recyclerView = view.findViewById(R.id.PresetRecyclerview)
        emptyPresetMessage = activity!!.findViewById<TextView>(R.id.empty_preset_message).apply {
            visibility = if (presets.isEmpty()) View.VISIBLE else View.INVISIBLE
        }

        //quick fix
        //locally converting String[] to ArrayList<String> for easy element removal
        adapter = PresetListAdapter(this.context!!, this::deletePresetFromDatabase)
        // Connect the adapter with the RecyclerView.
        recyclerView.adapter = adapter
        // Give the RecyclerView a default layout manager.
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        // start loading the presets
        loadPresetsFromDatabase()
        return view
    }

    //called to update the adapter
    fun updateDataSet() {
        val newPresets = loadPresetsFromJson()
        adapter.updatePresets(newPresets)
        emptyPresetMessage.visibility = if (newPresets.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    private fun savePresetsToDatabase(presets: List<Preset>) {
        // 1. opening the file
        val presetJson = File(context!!.filesDir, "presets.json")
        require(presetJson.exists() && presetJson.isFile)
        // 2. writing to file
        presetJson.writeText(
            jsonAdapter.toJson(presets),
            Charsets.UTF_16
        )
    }

    private fun deletePresetFromDatabase(preset: Preset) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                //Database.presets.presetDao().delete(preset)
            }
        }
    }

    private fun loadPresetsFromDatabase() {
        lifecycleScope.launch {
            //val presets = withContext(Dispatchers.IO) { Database.presets.presetDao().getAll() }
            val presets = List(5) { i ->
                Preset("Title $i", i * 5F, "Category $i")
            }
            adapter.updatePresets(presets)
        }
    }

    private fun loadPresetsFromJson(): List<Preset> {
        // 1. retrieve file
        val preset = File(context!!.filesDir, "preset.json")
        val presetFileExists = preset.exists()
        if (!presetFileExists) preset.createNewFile().also { successful -> require(successful) }
        // 2. read data from file
        return if (presetFileExists) {
            try {
                jsonAdapter.fromJson(preset.readText(Charsets.UTF_16)) ?: emptyList()
            } catch (e: IOException) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}