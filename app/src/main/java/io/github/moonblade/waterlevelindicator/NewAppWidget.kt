package io.github.moonblade.waterlevelindicator

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import io.github.moonblade.waterlevelindicator.Measurement.Measurement
import io.github.moonblade.waterlevelindicator.Settings.ChangeListener
import io.github.moonblade.waterlevelindicator.Settings.SettingsActivity

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    var measurement: Measurement? = null
    lateinit var appWidgetManager: AppWidgetManager
    var appWidgetId: Int = 0
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            measurement = Measurement.instance()
            this.appWidgetId = appWidgetId
            this.appWidgetManager = appWidgetManager
            updateAppWidget(context, appWidgetManager, appWidgetId, measurement)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    fun setValues(views: RemoteViews, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        Log.d("Widget", measurement?.widgetText)
        views.setTextViewText(R.id.widgetText, measurement?.widgetText)
        views.setProgressBar(R.id.widgetProgress, 100, measurement?.percentage?.toInt()!!, false)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    fun setListener(views: RemoteViews, context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {}
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        views.setOnClickPendingIntent(R.id.widgetProgress, pendingIntent)
        views.setOnClickPendingIntent(R.id.widgetText, pendingIntent)

        Log.d("Widget", "Setting listener for widget")
        measurement?.setWidgetOnChangeListener(object: ChangeListener {
            override fun changed() {
                setValues(views, appWidgetManager, appWidgetId)
            }
        })
    }

    fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, measurement: Measurement?) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)
        setValues(views, appWidgetManager, appWidgetId)
        setListener(views, context)
    }
}
