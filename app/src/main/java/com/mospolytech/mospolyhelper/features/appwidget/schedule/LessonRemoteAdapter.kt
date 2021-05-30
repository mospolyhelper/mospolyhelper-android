package com.mospolytech.mospolyhelper.features.appwidget.schedule

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.text.HtmlCompat
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.core.local.AppDatabase
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import com.mospolytech.mospolyhelper.domain.schedule.utils.fullTitle
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate


class LessonRemoteAdapter(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {
    companion object {
        private val lessonTypeColors = listOf(
            0xffe74c3c.toInt(),   // Exam, Credit,..
            0xff2e86c1.toInt()    // Other
        )

        private fun SpannableStringBuilder.appendAny(text: String, flags: Int, vararg spans: Any) {
            val start = length
            append(text)
            val length = length
            for (span in spans) {
                setSpan(span, start, length, flags)
            }
        }

        fun getTime(
            lesson: Lesson,
            time: LessonTimeUtils.LessonTimesStr,
            order: Int,
            showOrder: Boolean,
            showStartTime: Boolean,
            showEndTime: Boolean,
            showType: Boolean
        ): CharSequence {
            val builder = SpannableStringBuilder()
            val color2 =  (if (lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])

            val (timeStart, timeEnd) = time
            val res = StringBuilder()
            if (showOrder) {
                res.append("${order + 1}) ")
            }
            if (showStartTime) {
                res.append(timeStart)
            }
            if (showEndTime) {
                if (showStartTime) {
                    res.append(" - ")
                } else {
                    res.append("до ")
                }
                res.append(timeEnd)
            }
            builder.append(res)

            if (showType) {
                if (showStartTime || showEndTime) {
                    builder.append("  ")
                }
                builder.appendAny(
                    "\u00A0\u00A0${lesson.type.toLowerCase().replace(' ', '\u00A0')}\u00A0\u00A0",
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    ForegroundColorSpan(0xffffffff.toInt()),
                    BackgroundColorSpan(color2)
                )
            }

            return builder
        }

        fun getTitle(lesson: Lesson): CharSequence {
            return lesson.title
        }

        fun getTeachers(lesson: Lesson): CharSequence {
            return lesson.teachers.joinToString { it.getShortName() }
        }

        fun getGroups(lesson: Lesson): CharSequence {
            return lesson.groups.joinToString { it.title }
        }

        fun getAuditoriums(lesson: Lesson): CharSequence {
            return lesson.auditoriums.joinToString { parseAuditoriumTitle(it.fullTitle) }
        }

        private fun parseAuditoriumTitle(title: String): String {
            return SpannableString(
                HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            ).toString()
        }
    }

    private var dailySchedule: List<Pair<Lesson, LessonTime>>? = null

    private var showStartTime = true
    private var showEndTime = false
    private var showOrder = false
    private var showType = true
    private var showTeachers = false
    private var showGroups = false
    private var showAuditoriums = true

    override fun onCreate() {
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onDataSetChanged() {
        val localDataSource = ScheduleLocalDataSource(context.applicationContext)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val user = try {
            Json.decodeFromString<UserSchedule>(prefs.getString(
                PreferenceKeys.ScheduleUser,
                PreferenceDefaults.ScheduleUser
            )!!)
        } catch (e: Exception) {
            dailySchedule = null
            return
        }
        val schedule = runBlocking { localDataSource.get(user) }
        if (schedule == null) {
            dailySchedule = null
            return
        }
        dailySchedule = schedule.getLessons(LocalDate.now()).flatMap { lessonPlace ->
            lessonPlace.lessons.map { Pair(it, lessonPlace.time) }
        }
        showOrder = prefs.getBoolean("ScheduleAppwidgetShowOrder", false)
        showStartTime = prefs.getBoolean("ScheduleAppwidgetShowStartTime", true)
        showEndTime = prefs.getBoolean("ScheduleAppwidgetShowEndTime", false)
        showAuditoriums = prefs.getBoolean("ScheduleAppwidgetShowAuditoriums", true)
        showTeachers = prefs.getBoolean("ScheduleAppwidgetShowTeachers", false)
        showGroups = prefs.getBoolean("ScheduleAppwidgetShowGroups", false)
        showType = prefs.getBoolean("ScheduleAppwidgetShowType", true)
    }

    override fun hasStableIds() = true

    override fun getViewAt(position: Int): RemoteViews? {
        // TODO: Fix strange behaviour
        val remoteView = RemoteViews(context.packageName, R.layout.item_schedule_appwidget)
        val dailySchedule = dailySchedule
        if (dailySchedule == null) {
            remoteView.setTextViewText(R.id.textview_lesson_title, "Расписание не найдено")
            remoteView.setViewVisibility(R.id.text_lesson_time, View.GONE)
            remoteView.setViewVisibility(R.id.text_lesson_teachers, View.GONE)
            remoteView.setViewVisibility(R.id.textview_groups, View.GONE)
            remoteView.setViewVisibility(R.id.text_lesson_auditoriums, View.GONE)
            return remoteView
        }
        if (dailySchedule.isEmpty()) {
            remoteView.setTextViewText(R.id.textview_lesson_title, "Сегодня нет занятий")
            remoteView.setViewVisibility(R.id.text_lesson_time, View.GONE)
            remoteView.setViewVisibility(R.id.text_lesson_teachers, View.GONE)
            remoteView.setViewVisibility(R.id.textview_groups, View.GONE)
            remoteView.setViewVisibility(R.id.text_lesson_auditoriums, View.GONE)
            return remoteView
        }

        try {
            if (position in dailySchedule.indices)
            {
                val lesson = dailySchedule[position]

                setTime(remoteView, lesson)
                setTitle(remoteView, lesson)
                setTeachers(remoteView, lesson)
                setGroups(remoteView, lesson)
                setAuditoriums(remoteView, lesson)

                return remoteView
            } else {
                Log.d(TAG, "RemoteViewsFactory Index out of bounds: dailySchedule.size=${dailySchedule.size}, position=$position")
                return loadingView
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            return loadingView
        }
    }

    private fun setTime(view: RemoteViews, lesson: Pair<Lesson, LessonTime>) {
        if (showStartTime || showEndTime || showOrder || showType) {
            val time = getTime(
                lesson.first,
                lesson.second.timeString,
                lesson.second.order,
                showOrder,
                showStartTime,
                showEndTime,
                showType
            )
            view.setTextViewText(R.id.text_lesson_time, time)
            view.setViewVisibility(R.id.text_lesson_time, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.text_lesson_time, View.GONE)
        }
    }

    private fun setTitle(view: RemoteViews, lesson: Pair<Lesson, LessonTime>) {
        view.setTextViewText(R.id.textview_lesson_title, getTitle(lesson.first).toString())
    }

    private fun setTeachers(view: RemoteViews, lesson: Pair<Lesson, LessonTime>) {
        if (showTeachers) {
            view.setTextViewText(R.id.text_lesson_teachers, getTeachers(lesson.first))
            view.setViewVisibility(R.id.text_lesson_teachers, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.text_lesson_teachers, View.GONE)
        }
    }

    private fun setGroups(view: RemoteViews, lesson: Pair<Lesson, LessonTime>) {
        if (showGroups) {
            view.setTextViewText(R.id.textview_groups, getGroups(lesson.first))
            view.setViewVisibility(R.id.textview_groups, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.textview_groups, View.GONE)
        }
    }

    private fun setAuditoriums(view: RemoteViews, lesson: Pair<Lesson, LessonTime>) {
        if (showAuditoriums) {
            view.setTextViewText(R.id.text_lesson_auditoriums, getAuditoriums(lesson.first))
            view.setViewVisibility(R.id.text_lesson_auditoriums, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.text_lesson_auditoriums, View.GONE)
        }
    }

    override fun getCount(): Int {
        val dailySchedule = dailySchedule
        return if (dailySchedule == null || dailySchedule.isEmpty()) 1 else dailySchedule.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
    }
}