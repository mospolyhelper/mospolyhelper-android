package com.mospolytech.mospolyhelper.features.ui.schedule.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.utils.DefaultSettings
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleAppWidgetProvider : AppWidgetProvider() {
    companion object {
        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM")
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views = RemoteViews(
                context.packageName,
                R.layout.appwidget_schedule
            ).apply {
                val adapterIntent = Intent(context, LessonAdapterService::class.java)
                adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                setRemoteAdapter(R.id.list_schedule, adapterIntent)

                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                val groupTitle = prefs.getString(
                    PreferenceKeys.ScheduleGroupTitle,
                    DefaultSettings.ScheduleGroupTitle
                )!!
                val date = LocalDate.now().format(dateFormatter).capitalize()
                setTextViewText(R.id.text_lesson_date, "$date")
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_schedule)
        }
    }
}