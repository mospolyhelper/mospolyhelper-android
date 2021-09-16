package com.mospolytech.mospolyhelper.features.appwidget.schedule

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.AuditoriumScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
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
        val manager = AppWidgetManager.getInstance(context)

        if (intent.action == ACTION_APPWIDGET_UPDATE) {
//            val appWidgetId: Int = intent.getIntExtra(
//                AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID
//            )

            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                ?: IntArray(0)
            for (id in appWidgetIds) {
                manager.notifyAppWidgetViewDataChanged(id, R.id.list_schedule)
            }
        }

        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(
            context.packageName,
            R.layout.appwidget_schedule
        ).apply {
            val adapterIntent = Intent(context, LessonAdapterService::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
                }
            setRemoteAdapter(R.id.list_schedule, adapterIntent)

            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val user = try {
                Json.decodeFromString<ScheduleSource>(prefs.getString(
                    PreferenceKeys.ScheduleUser,
                    PreferenceDefaults.ScheduleUser
                )!!)
            } catch (e: Exception) {
                null
            }
            val userTitle = when (user) {
                is StudentScheduleSource -> user.title
                is TeacherScheduleSource -> Teacher(user.title).getShortName()
                is AuditoriumScheduleSource -> user.title
                else -> ""
            }
            val date = LocalDate.now().format(dateFormatter).capitalize()
            setTextViewText(R.id.text_lesson_date, "$date | $userTitle")
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}