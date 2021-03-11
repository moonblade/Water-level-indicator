package io.github.moonblade.waterlevelindicator.Settings

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import io.github.moonblade.waterlevelindicator.DataBase
import kotlin.math.abs

class Settings {
    private var listener: ChangeListener? = null
    var anomalyDistanceLimit = 20
    var minimumValue = -1
    var maximumValue = -1
    var autoUpdateMinMax = true
    var brightness = 8;
    var usePercentDisplay = true;

    fun updateFields(hashMap: HashMap<String, Int>) {
        Log.d("Settings", "Updating settings: $hashMap")
        val _minimumValue = hashMap?.get("minimumValue")
        if (_minimumValue != null && _minimumValue != minimumValue) {
            minimumValue = _minimumValue
        }

        val _maximumValue = hashMap?.get("maximumValue")
        if (_maximumValue != null && _minimumValue != maximumValue) {
            maximumValue = _maximumValue
        }


        val _autoUpdateMinMax = hashMap?.get("autoUpdateMinMax")
        if (_autoUpdateMinMax != null && (_autoUpdateMinMax > 0) != autoUpdateMinMax) {
            autoUpdateMinMax = (_autoUpdateMinMax > 0)
        }

        val _anomalyDistanceLimit = hashMap?.get("anomalyDistanceLimit")
        if (_anomalyDistanceLimit != null && _anomalyDistanceLimit != anomalyDistanceLimit) {
            anomalyDistanceLimit = _anomalyDistanceLimit
        }

        val _brightness = hashMap?.get("brightness")
        if (_brightness != null && _brightness != brightness) {
            brightness = _brightness
        }

        val _usePercentDisplay = hashMap?.get("printMode")
        if (_usePercentDisplay != null && (_usePercentDisplay > 0) != usePercentDisplay) {
            usePercentDisplay = (_usePercentDisplay > 0)
        }

        if (listener != null) {
            listener!!.changed()
        }
    }

    constructor() {
        Log.d("Settings", "Initializing settings")
        DataBase.instance()?.reference?.child("settings")?.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val hashMap = snapshot.getValue<HashMap<String, Int>>()
                hashMap?.let { updateFields(it) }
            }

        })
    }

    public fun setOnChangeListener(listener: ChangeListener) {
        this.listener = listener
    }

    fun updateMinMax(value: Int) {
        if (autoUpdateMinMax) {
            if (value < minimumValue && abs(value - minimumValue) < anomalyDistanceLimit)
                minimumValue = value
            if (value > maximumValue && abs(value - maximumValue) < anomalyDistanceLimit)
                maximumValue = value
            if (minimumValue != -1 && maximumValue != -1)
                pushValues()
        }

    }

    fun pushValues() {
        DataBase.instance()?.updateSettingInt("autoUpdateMinMax", if(autoUpdateMinMax) 1 else 0)
        DataBase.instance()?.updateSettingInt("printMode", if(usePercentDisplay) 1 else 0)
        DataBase.instance()?.updateSettingInt("brightness", brightness)
        DataBase.instance()?.updateSettingInt("maximumValue", maximumValue)
        DataBase.instance()?.updateSettingInt("minimumValue", minimumValue)
        DataBase.instance()?.updateSettingInt("anomalyDistanceLimit", anomalyDistanceLimit)
    }
}