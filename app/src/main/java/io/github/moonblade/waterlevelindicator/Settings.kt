package io.github.moonblade.waterlevelindicator

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class Settings {
    var minimumValue = 30
    set(value) {
        field = value
        DataBase.instance()?.updateSettingInt("minimumValue", value)
    }

    var maximumValue = 40
    set(value) {
        field = value
        DataBase.instance()?.updateSettingInt("maximumValue", value)
    }

    var autoUpdateMinMax = true
    set(value) {
        field = value
        DataBase.instance()?.updateSettingInt("autoUpdateMinMax", if(value) 1 else 0)
    }

    var anomalyDistanceLimit = 20
    set(value) {
        field = value
        DataBase.instance()?.updateSettingInt("anomalyDistanceLimit", value)
    }

    fun updateFields(hashMap: HashMap<String, Int>) {
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
    }

    init {
        DataBase.instance()?.reference?.child("settings")?.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val hashMap = snapshot.getValue<HashMap<String, Int>>()
                hashMap?.let { updateFields(it) }
            }

        })
    }
}