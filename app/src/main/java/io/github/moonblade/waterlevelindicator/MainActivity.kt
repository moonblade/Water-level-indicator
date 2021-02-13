package io.github.moonblade.waterlevelindicator

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.CompoundButton
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settings = Settings()
        settings.minimumValue = 35
        settings.autoUpdateMinMax = true
        val database = Firebase.database
        val reference = database.getReference("waterlevel")
        val text = findViewById<TextView>(R.id.textView)
        val lastUpdate = findViewById<TextView>(R.id.lastUpdate)
        val minVal = findViewById<EditText>(R.id.minValue)
        val maxVal = findViewById<EditText>(R.id.maxValue)
        val progress = findViewById<ProgressBar>(R.id.percentage)
        val autoUpdateToggle = findViewById<SwitchCompat>(R.id.autoUpdateToggle)

        val sharedPref = getSharedPreferences("sharedPref2", Context.MODE_PRIVATE)
        var minValue = sharedPref.getInt("minValue", 40)
        var maxValue = sharedPref.getInt("maxValue", 40)
        val autoUpdate = sharedPref.getBoolean("autoUpdate", true)
        minVal.setText(minValue.toString())
        maxVal.setText(maxValue.toString())
        autoUpdateToggle.isChecked = autoUpdate;
        minVal.isVisible = !autoUpdate
        maxVal.isVisible = !autoUpdate

        minVal.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    with (sharedPref.edit()) {
                        putInt("minValue", s.toString().toInt())
                        apply()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        maxVal.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    with(sharedPref.edit()) {
                        putInt("maxValue", s.toString().toInt())
                        apply()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<HashMap<String, Long>>()
                Log.d("waterLevel", "Value is: $value")
                val measurement = value?.get("measurement")
                val lastTime = getDateTime(value?.get("timestamp"))
                val percentage = 100 - ((measurement?.toInt()!! - minValue) * 100 )/ max((maxValue - minValue), 1)
                text.setText("Value: $measurement, Percentage: $percentage")
                lastUpdate.setText("Last updated: $lastTime")
                progress.max = 100
                progress.setProgress(percentage)

                if (autoUpdate) {
                    if (measurement?.toInt() > maxValue) {
                        maxValue = measurement?.toInt()
                        maxVal.setText(maxValue.toString())
                        with(sharedPref.edit()) {
                            putInt("maxValue", maxValue)
                            apply()
                        }
                    }

                    if (measurement?.toInt() < minValue) {
                        minValue = measurement?.toInt()
                        minVal.setText(minValue.toString())
                        with(sharedPref.edit()) {
                            putInt("minValue", minValue)
                            apply()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("waterLevel", "Failed to read value.", error.toException())
            }
        })

        autoUpdateToggle.setOnCheckedChangeListener{ buttonView, isChecked ->
            Log.d("waterLevel", "Checked value changed$isChecked")
            with(sharedPref.edit()) {
                putBoolean("autoUpdate", isChecked)
                minVal.isVisible = !isChecked
                maxVal.isVisible = !isChecked
                apply()
            }
        }
    }

    private fun getDateTime(s: Long?): Any {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy h:m:s a")
            val netDate = s?.let { Date(it) }
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }


}