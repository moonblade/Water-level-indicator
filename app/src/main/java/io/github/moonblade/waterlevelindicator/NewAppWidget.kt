package io.github.moonblade.waterlevelindicator

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)

    val database = Firebase.database
    val reference = database.getReference("waterlevel")

    val sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    val minValue = sharedPref.getInt("minValue", 0)
    val maxValue = sharedPref.getInt("maxValue", 100)
    val autoUpdate = sharedPref.getBoolean("autoUpdate", true)
    Log.d("waterLevel", "Updating app widget")

    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            val value = dataSnapshot.getValue<HashMap<String, Long>>()
            Log.d("waterLevel", "Value is: $value")
            val measurement = value?.get("measurement")
            val percentage = 100 - ((measurement?.toInt()!! - minValue) * 100 )/ (maxValue - minValue)
            Log.d("waterLevel", "Setting measurement percentage $percentage")
            views.setProgressBar(R.id.measurement, 100, percentage, false)
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("waterLevel", "Failed to read value.", error.toException())
        }
    })

}