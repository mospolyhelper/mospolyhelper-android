package com.mospolytech.mospolyhelper.features.appwidget.schedule

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.teacher.Teacher
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ScheduleAppWidgetProvider : AppWidgetProvider() {
    companion object {
        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d MMM")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Проверяем, что это intent от нажатия на третью зону
        if (intent.action.equals(ACTION_APPWIDGET_UPDATE, ignoreCase = true)) {

            // извлекаем ID экземпляра
            var appWidgetIds: IntArray = IntArray(0)
            val extras = intent.extras
            if (extras != null) {
                appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS) ?: IntArray(0)
            }
            for (id in appWidgetIds) {
                AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(id, R.id.list_schedule)
//                updateWidget(
//                    context,
//                    AppWidgetManager.getInstance(context),
//                    id
//                )
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
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
            val user = try {
                Json.decodeFromString<UserSchedule>(prefs.getString(
                    PreferenceKeys.ScheduleUser,
                    PreferenceDefaults.ScheduleUser
                )!!)
            } catch (e: Exception) {
                null
            }
            val userTitle = when (user) {
                is StudentSchedule -> user.title
                is TeacherSchedule -> Teacher(user.title).getShortName()
                is AuditoriumSchedule -> user.title
                else -> ""
            }
            val date = LocalDate.now().format(dateFormatter).capitalize()
            setTextViewText(R.id.text_lesson_date, "$date | $userTitle")
        }

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
        //
    }
}