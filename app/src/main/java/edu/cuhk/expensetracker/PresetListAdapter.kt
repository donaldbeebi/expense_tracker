package edu.cuhk.expensetracker

import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cuhk.expensetracker.PresetListAdapter.PresetViewHolder
import android.view.LayoutInflater
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import edu.cuhk.expensetracker.room.preset.Preset

@Deprecated("No longer needed")
class PresetListAdapter(
    val context: Context,
    val deletePresetFromDatabase: (Preset) -> Unit
) : RecyclerView.Adapter<PresetViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private var presets = ArrayList<Preset>()//PresetList(emptyList())

    inner class PresetViewHolder(itemView: View, adapter: PresetListAdapter) : RecyclerView.ViewHolder(itemView) {
        val mAdapter: PresetListAdapter
        var mPresetCategoryTextView: TextView
        var mPresetTitleTextView: TextView
        var mPresetAmountTextView: TextView
        var mPresetItemButton: Button
        var mDeleteButton: ImageView

        lateinit var preset: Preset

        init {
            mPresetTitleTextView = itemView.findViewById(R.id.PresetTitle)
            mPresetAmountTextView = itemView.findViewById(R.id.PresetAmount)
            mPresetCategoryTextView = itemView.findViewById(R.id.PresetCategory)
            mDeleteButton = itemView.findViewById(R.id.deleteButton)
            mDeleteButton.setOnClickListener { view: View? ->
                presets.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                deletePresetFromDatabase(presets[adapterPosition])
                /*var `is`: FileInputStream? = null
                var isd: FileInputStream? = null
                try {
                    isd = FileInputStream(FILE_PATH_dup)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                val br = BufferedReader(InputStreamReader(isd, Charset.forName("UTF-8")))
                var fos: FileOutputStream? = null
                var line = ""
                try {
                    fos = mContext.openFileOutput("preset", Context.MODE_PRIVATE)
                    var count = 0
                    while (br.readLine().also { line = it } != null) {
                        if (count == Integer.valueOf(position)) {
                            count++
                            continue
                        }
                        val output = """
                        $line
                        
                        """.trimIndent()
                        fos.write(output.toByteArray())
                        count++
                    }
                } catch (fileNotFoundException: FileNotFoundException) {
                    fileNotFoundException.printStackTrace()
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                }

                //overwrite duplicate file
                try {
                    `is` = FileInputStream(FILE_PATH)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                val brd = BufferedReader(InputStreamReader(`is`, Charset.forName("UTF-8")))
                var fosd: FileOutputStream? = null
                try {
                    fosd = mContext.openFileOutput("preset_dup", Context.MODE_PRIVATE)
                    while (brd.readLine().also { line = it } != null) {
                        val output = """
                        $line
                        
                        """.trimIndent()
                        fosd.write(output.toByteArray())
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                //quick fix
                //local update -> no need to restart activity to see the deletion
                mPresetItem.removeAt(layoutPosition)
                mPresetAmount.removeAt(layoutPosition)
                mPresetCategory.removeAt(layoutPosition)

                //updating the message
                if (mPresetItem.isEmpty()) mEmptyPresetMessage.visibility = View.VISIBLE
                notifyItemRemoved(layoutPosition)*/
            }
            mPresetItemButton = itemView.findViewById(R.id.PresetItemSelectButton)
            mPresetItemButton.setOnClickListener {

                val intent = Intent(this@PresetListAdapter.context, InputActivity::class.java)
                with (presets[adapterPosition]) {
                    intent.putExtra("input", title)
                    intent.putExtra("PresetAmount", amount)
                    intent.putExtra("PresetCategory", category)
                }
                this@PresetListAdapter.context.startActivity(intent)
            }
            mAdapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val mItemView = inflater.inflate(R.layout.presetlist_item, parent, false)
        return PresetViewHolder(mItemView, this)
    }

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        Log.d("PresetListAdapter", "onBindViewHolder called with position $position")
        with (presets[position]) {
            holder.mPresetTitleTextView.text = title
            holder.mPresetAmountTextView.text = amount.toString()
            holder.mPresetCategoryTextView.text = category
            holder.preset = this
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return presets.size
    }

    //custom function called when the preset fragment is resumed
    //this is to update the data set after adding a preset
    fun updatePresets(presets: List<Preset>) {
        this.presets.clear()
        this.presets += presets
        notifyDataSetChanged()
    }
}