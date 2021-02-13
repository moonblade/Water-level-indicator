package io.github.moonblade.waterlevelindicator

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DataBase {
    internal var reference = Firebase.database.reference

    constructor() {
    }

    companion object {
        private var singleInstance: DataBase? = null
        public fun instance(): DataBase? {
            if (this.singleInstance == null) {
                this.singleInstance = DataBase()
            }
            return this.singleInstance
        }
    }

    public fun updateSettingInt(key: String, value: Int) {
        reference.child("settings").child(key).setValue(value)
    }
}
