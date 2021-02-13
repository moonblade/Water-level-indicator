package io.github.moonblade.waterlevelindicator.Settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import io.github.moonblade.waterlevelindicator.R

class SettingsActivity : AppCompatActivity() {
    private var autoUpdate: SwitchCompat? = null
    private var anomalyDistance: EditText? = null
    private var maxVal: EditText? = null
    private var minVal: EditText? = null
    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        connect()
        initialise()
        setListeners()
    }

    private fun setListeners() {
        settings.setOnChangeListener(object:
            SettingsChangeListener {
            override fun settingsChanged() {
                setValues()
            }
        })
    }

    private fun connect() {
        minVal = findViewById<EditText>(R.id.minValue)
        maxVal = findViewById<EditText>(R.id.maxValue)
        anomalyDistance = findViewById<EditText>(R.id.anomalyDistance)
        autoUpdate = findViewById<SwitchCompat>(R.id.autoUpdateToggle)
    }

    private fun initialise() {
        settings = Settings();
        setValues()
    }

    private  fun setValues() {
        minVal?.setText(settings.minimumValue.toString())
        maxVal?.setText(settings.maximumValue.toString())
        anomalyDistance?.setText(settings.maximumValue.toString())
        autoUpdate?.isChecked = settings.autoUpdateMinMax
    }
}