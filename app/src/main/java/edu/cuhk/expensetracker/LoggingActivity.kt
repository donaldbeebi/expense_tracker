package edu.cuhk.expensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import android.widget.Button as XmlButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.appcompattheme.AppCompatTheme
import edu.cuhk.expensetracker.room.DB
import edu.cuhk.expensetracker.room.preset.Preset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoggingActivity : AppCompatActivity() {
    private var presetListState: PresetListState by mutableStateOf(PresetListState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logging)
        // calling the action bar
        val actionBar = supportActionBar
        // showing the back button in action bar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val startInputButton = findViewById<XmlButton>(R.id.StartInputButton)
        //EditText input= findViewById((R.id.Manual_Input));

        //adding underline for the title text
        //TextView PresetTitleItemTextView= findViewById(R.id.PresetTitleItemTextView);
        //TextView PresetTitleAmountTextView=findViewById(R.id.PresetTitleAmountTextView);
        //TextView PresetTitleCategoryTextView= findViewById(R.id.PresetTitleCategoryTextView);
        //PresetTitleAmountTextView.setPaintFlags(PresetTitleAmountTextView.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
        //PresetTitleItemTextView.setPaintFlags(PresetTitleItemTextView.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
        //PresetTitleCategoryTextView.setPaintFlags(PresetTitleCategoryTextView.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);

        //initializing fragment
        /*presetFragment = PresetFragment().apply {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.PresetFragment, this)
            transaction.commit()
        }*/

        //onClick for logging with Manual Input
        startInputButton.setOnClickListener {
            val intent = Intent(this@LoggingActivity, InputActivity::class.java)
            startActivity(intent)
        }

        /*val dataList = SnapshotStateList<Pair<Preset, Boolean>>().apply {
            for (i in 0..12) {
                this += Pair(
                    Preset(
                        "Title $i",
                        i * 5F,
                        "Category $i"
                    ),
                    true
                )
            }
        }*/

        findViewById<ComposeView>(R.id.PresetList).setContent {
            AppCompatTheme {
                PresetList(presetListState)
            }
        }

        loadPresets()
    }

    private fun loadPresets() = lifecycleScope.launch(Dispatchers.IO) {
        val presets = DB.presetDao().getAll()
        val dataList = SnapshotStateList<Pair<Preset, Boolean>>()
        dataList.addAll(presets.map { Pair(it, true) })
        withContext(Dispatchers.Main) {
            this@LoggingActivity.presetListState = PresetListState.Ready(
                dataList,
                onRemovePreset = { i ->
                    val dataToRemove = dataList[i]
                    dataList[i] = dataToRemove.copy(second = false)
                    lifecycleScope.launch(Dispatchers.IO) {
                        DB.presetDao().delete(dataToRemove.first)
                    }
                },
                onSelectPreset = { i -> onPresetSelect(dataList[i].first) }
            )
        }
    }

    // this event will enable the back function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun startNewPreset(view: View?) {
        val intent = Intent(this@LoggingActivity, AddPresetActivity::class.java)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //presetFragment.updateDataSet()
                loadPresets()
            }
        }
    }

    private fun onPresetSelect(preset: Preset) {
        val intent = Intent(this, InputActivity::class.java)
        with (preset) {
            intent.putExtra("input", title)
            intent.putExtra("PresetAmount", amount)
            intent.putExtra("PresetCategory", category)
        }
        startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
private fun PresetListPreview() {
    val dataList = remember {
        SnapshotStateList<Pair<Preset, Boolean>>().apply {
            for (i in 0..8) {
                this += Pair(
                    Preset(
                        "Title $i",
                        i * 5F,
                        "Category $i"
                    ),
                    true
                )
            }
        }
    }
    PresetList(PresetListState.Ready(dataList, {}, {}))
}

@Composable
private fun PresetList(
    state: PresetListState
) {
    Surface(
        border = BorderStroke(2.dp, Color.Gray),
        shape = RoundedCornerShape(8.dp)
    ) {
        when (state) {
            is PresetListState.Loading -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is PresetListState.Ready -> LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                item { Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(16.dp)) }
                state.dataList.forEachIndexed { i, data ->
                    item {
                        PresetItem(
                            data,
                            { state.onRemovePreset(i) },
                            { state.onSelectPreset(i) }
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun PresetItem(
    data: Pair<Preset, Boolean>,
    onRemovePreset: () -> Unit,
    onSelectPreset: () -> Unit
) {
    val (preset, exists) = data
    AnimatedVisibility(exists) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1F)
                ) {
                    Text(
                        text = preset.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = preset.category,
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "$%.1f".format(preset.amount),
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Monospace
                )
                Image(
                    painter = painterResource(R.drawable.ic_menu_delete_button_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.5F)
                        .aspectRatio(1F)
                        .clickable(onClick = onRemovePreset)
                )
                Button(
                    onClick = onSelectPreset,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "SELECT",
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp))
        }
    }
}

private sealed class PresetListState {
    object Loading : PresetListState()
    class Ready(
        val dataList: List<Pair<Preset, Boolean>>,
        val onRemovePreset: (Int) -> Unit,
        val onSelectPreset: (Int) -> Unit
    ) : PresetListState()
}