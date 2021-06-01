package com.mospolytech.mospolyhelper.features.broadcast_receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import androidx.core.text.HtmlCompat
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity

class ScheduleCurrentLessonBroadcastReceiver : BroadcastReceiver() {

    private val dataSource = ScheduleLocalDataSource(App.context!!)

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

//        GlobalScope.async {
//            val lessons = getCurrentLessons(context)
//            val lessonSize = if (lessons.first.size > NotificationIds.SCHEDULE_CURRENT_LESSON.size) {
//                NotificationIds.SCHEDULE_CURRENT_LESSON.size
//            } else {
//                lessons.first.size
//            }
//            val manager = NotificationManagerCompat.from(context)
//            for (i in 0..lessonSize) {
//                val builder = NotificationCompat.Builder(context, NotificationChannelIds.SCHEDULE_CURRENT_LESSON)
//                    .setSmallIcon(R.drawable.ic_launcher_background)
//                    .setContentTitle("My notification")
//                    .setContentText("Hello World!")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setContentIntent(pendingIntent)
//                manager.notify(NotificationIds.SCHEDULE_CURRENT_LESSON[i], builder.build())
//            }
//            for (i in (lessonSize + 1) until NotificationIds.SCHEDULE_CURRENT_LESSON.size) {
//                manager.cancel(NotificationIds.SCHEDULE_CURRENT_LESSON[i])
//            }
//        }
    }

    private fun parseAuditoriumTitle(title: String): String {
        val str = SpannableString(
            HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
        ).toString()
        val index = str.indexOfFirst { it.isLetterOrDigit() }
        return if (index == -1) str else str.substring(index)
    }

//    private suspend fun getCurrentLessons(context: Context): Pair<List<Lesson>, Boolean> {
//        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
//        val groupTitle = prefs.getString(
//            PreferenceKeys.ScheduleUser,
//            PreferenceDefaults.ScheduleUser
//        )!!
//        var isSession = true
//        var schedule = dataSource.get(StudentSchedule(groupTitle, groupTitle))
//        if (schedule == null) {
//            isSession = false
//            schedule = dataSource.get(StudentSchedule(groupTitle, groupTitle))
//            if (schedule == null) {
//                return Pair(listOf(), false)
//            }
//
//        }
//        val date = LocalDate.now(ZoneId.of("Europe/Moscow"))
//        var dailySchedule = schedule.getLessons(date)
//        if (dailySchedule.isEmpty() && isSession) {
//            schedule = dataSource.get(StudentSchedule(groupTitle, groupTitle))
//            if (schedule == null) {
//                return Pair(listOf(), false)
//            }
//        }
//        dailySchedule = schedule.getLessons(date)
//        if (dailySchedule.isEmpty()) {
//            return Pair(listOf(), false)
//        }
//        val time = LocalTime.now(ZoneId.of("Europe/Moscow"))
//        val order = Lesson.getOrder(time, dailySchedule.firstOrNull()?.groupIsEvening ?: false)
//        val currentLessons = dailySchedule.filter { it.order == order.order }
//        return Pair(currentLessons, order.isStarted)
//    }
}