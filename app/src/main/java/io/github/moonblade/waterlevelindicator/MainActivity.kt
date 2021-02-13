package io.github.moonblade.waterlevelindicator

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import io.github.moonblade.waterlevelindicator.Measurement.Measurement
import io.github.moonblade.waterlevelindicator.Settings.ChangeListener
import io.github.moonblade.waterlevelindicator.Settings.Settings
import io.github.moonblade.waterlevelindicator.Settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    private var percentageProgress: ProgressBar? = null
    private var lastUpdate: TextView? = null
    private var distance: TextView? = null
    private var percentageText: TextView? = null
    private var measurement: Measurement? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()
        setListeners()
    }

    private fun setListeners() {
        measurement?.setOnChangeListener(object: ChangeListener{
            override fun changed() {
                setValues()
            }
        })
    }

    private fun setValues() {
        lastUpdate?.setText(measurement?.lastUpdateString)
        distance?.setText(measurement?.distanceString)
        percentageText?.setText(measurement?.percentageString)
        measurement?.percentage?.toInt()?.let { percentageProgress?.setProgress(it) };

        val man = AppWidgetManager.getInstance(this)
        val ids = man.getAppWidgetIds(ComponentName(this, NewAppWidget::class.java))
        val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(updateIntent)
    }

    private fun initialize() {
        lastUpdate = findViewById<TextView>(R.id.lastUpdate)
        distance = findViewById<TextView>(R.id.distance)
        percentageText = findViewById<TextView>(R.id.percentageText)
        percentageProgress = findViewById<ProgressBar>(R.id.percentage)
        measurement = Measurement.instance()!!
        setValues()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_activity, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java).apply {}
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}