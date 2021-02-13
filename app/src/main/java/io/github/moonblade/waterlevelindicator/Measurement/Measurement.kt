package io.github.moonblade.waterlevelindicator.Measurement

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import io.github.moonblade.waterlevelindicator.DataBase
import io.github.moonblade.waterlevelindicator.Settings.ChangeListener
import io.github.moonblade.waterlevelindicator.Settings.Settings
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Measurement {
    var percentageString: String = ""
    var percentage: Long = 0
    var lastUpdateString: String = ""
    var fullTimeStamp: Any = ""
    var halfTimeStamp: Any = ""
    var listener: ChangeListener? = null
    var widgetListener: ChangeListener? = null
    var distanceString: String = ""
    var settings: Settings? = null
    var widgetText: String = ""
    var distance: Long = 0
    set(value) {
        if (distance.toInt() == 0 || abs(value - distance) < settings?.anomalyDistanceLimit!!) {
            field = value
            distanceString = "Distance to water level: $value cm"
    //        percentage = 100 - (((value - min) * 100 )/ (max - min))
            percentage = (100 - (((value.toInt() - settings?.minimumValue!!) * 100) / max((settings?.maximumValue!! - settings?.minimumValue!!), 1))).toLong()
            percentage = max(min(100, percentage), 0)
            Log.d("Measurement", "Settings percentage $percentage as $value - ${settings?.minimumValue} * 100 / ${settings?.maximumValue}  - ${settings?.minimumValue}")
            percentageString = "Water level percentage: $percentage"

            settings!!.updateMinMax(value.toInt())
        }

    }

    var timestamp: Long = 0
    set(value) {
        field = value
        fullTimeStamp = getDateTime(value, null)
        halfTimeStamp = getDateTime(value, "h:mm:s")
        lastUpdateString = "Last updated at: $fullTimeStamp"

        widgetText = "$percentage%: $halfTimeStamp"
    }

    companion object {
        var _instance: Measurement? = null;
        fun instance(): Measurement? {
            if (_instance == null) {
                _instance = Measurement()
            }
            return _instance;
        }
    }

    constructor() {
        Log.d("Measurement", "Initializing measurement")
        settings = Settings()
        DataBase.instance()?.reference?.child("waterlevel")?.addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val hashMap = snapshot.getValue<HashMap<String, Long>>()
                hashMap?.let { updateFields(it) }
            }

        })
    }

    private fun updateFields(hashMap: HashMap<String, Long>) {
        Log.d("Measurement", "Updating measurement: $hashMap")
        val _distance = hashMap?.get("measurement")
        if (_distance != null && _distance != distance) {
            distance = _distance
        }

        val _timestamp = hashMap?.get("timestamp")
        if (_timestamp != null && _timestamp != timestamp) {
            timestamp = _timestamp
        }
        if (listener != null) {
            Log.d("Measurement", "Updating app listener")
            listener!!.changed()
        }

        if (widgetListener != null) {
            Log.d("Measurement", "Updating widget listener")
            widgetListener!!.changed()
        }
    }

    fun setOnChangeListener(listener: ChangeListener) {
        this.listener = listener
    }

    fun setWidgetOnChangeListener(listener: ChangeListener) {
        Log.d("Measurement", "Setting widget listener")
        this.widgetListener = listener
    }

    private fun getDateTime(s: Long?, _format: String?): Any {
        var format = _format
        if (format == null) 
            format = "dd/MM/yyyy h:m:s a"
        try {
            val sdf = SimpleDateFormat(format)
            val netDate = s?.let { Date(it) }
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

}