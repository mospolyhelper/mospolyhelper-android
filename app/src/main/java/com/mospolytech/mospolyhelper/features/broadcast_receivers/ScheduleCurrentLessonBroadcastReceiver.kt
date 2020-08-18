package com.mospolytech.mospolyhelper.features.broadcast_receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleLocalConverter
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.utils.DefaultSettings
import com.mospolytech.mospolyhelper.utils.NotificationChannelIds
import com.mospolytech.mospolyhelper.utils.NotificationIds
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class ScheduleCurrentLessonBroadcastReceiver : BroadcastReceiver() {

    private val dataSource = ScheduleLocalDataSource(ScheduleLocalConverter())

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent
            .getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        GlobalScope.async {
            val lessons = getCurrentLessons(context)
            val lessonSize = if (lessons.first.size > NotificationIds.SCHEDULE_CURRENT_LESSON.size) {
                NotificationIds.SCHEDULE_CURRENT_LESSON.size
            } else {
                lessons.first.size
            }
            val manager = NotificationManagerCompat.from(context)
            for (i in 0..lessonSize) {
                val builder = NotificationCompat.Builder(context, NotificationChannelIds.SCHEDULE_CURRENT_LESSON)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("My notification")
                    .setContentText("Hello World!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                manager.notify(NotificationIds.SCHEDULE_CURRENT_LESSON[i], builder.build())
            }
            for (i in (lessonSize + 1) until NotificationIds.SCHEDULE_CURRENT_LESSON.size) {
                manager.cancel(NotificationIds.SCHEDULE_CURRENT_LESSON[i])
            }
        }
    }

    private fun parseAuditoriumTitle(title: String): String {
        val str = SpannableString(
            HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
        ).toString()
        val index = str.indexOfFirst { it.isLetterOrDigit() }
        return if (index == -1) str else str.substring(index)
    }

    private suspend fun getCurrentLessons(context: Context): Pair<List<Lesson>, Boolean> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val groupTitle = prefs.getString(
            PreferenceKeys.ScheduleGroupTitle,
            DefaultSettings.ScheduleGroupTitle
        )!!
        var isSession = true
        var schedule = dataSource.get(groupTitle, isSession)
        if (schedule == null) {
            isSession = false
            schedule = dataSource.get(groupTitle, isSession)
            if (schedule == null) {
                return Pair(listOf(), false)
            }

        }
        val date = LocalDate.now(ZoneId.of("Europe/Moscow"))
        var dailySchedule = schedule.getSchedule(date)
        if (dailySchedule.isEmpty() && isSession) {
            schedule = dataSource.get(groupTitle, isSession)
            if (schedule == null) {
                return Pair(listOf(), false)
            }
        }
        dailySchedule = schedule.getSchedule(date)
        if (dailySchedule.isEmpty()) {
            return Pair(listOf(), false)
        }
        val time = LocalTime.now(ZoneId.of("Europe/Moscow"))
        val order = Lesson.getOrder(time, schedule.group.isEvening)
        val currentLessons = dailySchedule.filter { it.order == order.first }
        return Pair(currentLessons, order.second)
    }
}