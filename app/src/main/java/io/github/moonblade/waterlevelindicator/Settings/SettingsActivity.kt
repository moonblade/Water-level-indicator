package io.github.moonblade.waterlevelindicator.Settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.OnClickAction
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import io.github.moonblade.waterlevelindicator.R

class SettingsActivity : AppCompatActivity() {
    private var autoUpdate: SwitchCompat? = null
    private var anomalyDistance: EditText? = null
    private var maxVal: EditText? = null
    private var minVal: EditText? = null
    private var save: Button? = null
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
            ChangeListener {
            override fun changed() {
                setValues()
            }
        })

        minVal?.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 0 && !s.toString().equals("-"))
                    settings.minimumValue = s.toString().toInt()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        maxVal?.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 0 && !s.toString().equals("-"))
                    settings.maximumValue = s.toString().toInt()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        anomalyDistance?.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 0)
                    settings.anomalyDistanceLimit = s.toString().toInt()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        autoUpdate?.setOnCheckedChangeListener { _, isChecked ->
            settings.autoUpdateMinMax = isChecked
        }

        save?.setOnClickListener {
            settings.pushValues()
        }
    }

    private fun connect() {
        minVal = findViewById<EditText>(R.id.minValue)
        maxVal = findViewById<EditText>(R.id.maxValue)
        anomalyDistance = findViewById<EditText>(R.id.anomalyDistance)
        autoUpdate = findViewById<SwitchCompat>(R.id.autoUpdateToggle)
        save = findViewById<Button>(R.id.save)
    }

    private fun initialise() {
        settings = Settings();
        setValues()
    }

    private  fun setValues() {
        if (minVal?.text.toString() != settings.minimumValue.toString())
            minVal?.setText(settings.minimumValue.toString())
        if (maxVal?.text.toString() != settings.maximumValue.toString())
            maxVal?.setText(settings.maximumValue.toString())
        if (anomalyDistance?.text.toString() != settings.anomalyDistanceLimit.toString())
            anomalyDistance?.setText(settings.anomalyDistanceLimit.toString())
        autoUpdate?.isChecked = settings.autoUpdateMinMax
    }
}