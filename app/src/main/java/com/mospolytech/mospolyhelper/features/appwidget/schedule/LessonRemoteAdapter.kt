package com.mospolytech.mospolyhelper.features.appwidget.schedule

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.text.HtmlCompat
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
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
            showOrder: Boolean,
            showStartTime: Boolean,
            showEndTime: Boolean,
            showType: Boolean
        ): CharSequence {
            val builder = SpannableStringBuilder()
            val color2 =  (if (lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])

            val (timeStart, timeEnd) = lesson.time
            val res = StringBuilder()
            if (showOrder) {
                res.append("${lesson.order + 1}) ")
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

        fun getAuditoriums(lesson: Lesson): CharSequence {
            return lesson.auditoriums.joinToString { parseAuditoriumTitle(it.title) }
        }

        private fun parseAuditoriumTitle(title: String): String {
            return SpannableString(
                HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            ).toString()
        }
    }

    private var dailySchedule: List<Lesson>? = null

    private var showStartTime = true
    private var showEndTime = false
    private var showOrder = false
    private var showType = true
    private var showTeachers = false
    private var showAuditoriums = true

    override fun onCreate() {
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onDataSetChanged() {
        val localDataSource =
            ScheduleLocalDataSource()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val id = prefs.getString(
            PreferenceKeys.ScheduleGroupTitle,
            PreferenceDefaults.ScheduleGroupTitle
        )!!
        val isStudent = prefs.getBoolean(
            PreferenceKeys.ScheduleUserTypePreference,
            PreferenceDefaults.ScheduleUserTypePreference
        )
        val schedule = localDataSource.get(id, isStudent)
        if (schedule == null) {
            dailySchedule = null
            return
        }
        dailySchedule = schedule.getSchedule(LocalDate.now())
        showOrder = prefs.getBoolean("ScheduleAppwidgetShowOrder", true)
        showStartTime = prefs.getBoolean("ScheduleAppwidgetShowStartTime", true)
        showEndTime = prefs.getBoolean("ScheduleAppwidgetShowEndTime", true)
        showAuditoriums = prefs.getBoolean("ScheduleAppwidgetShowAuditoriums", true)
        showTeachers = prefs.getBoolean("ScheduleAppwidgetShowTeachers", true)
        showType = prefs.getBoolean("ScheduleAppwidgetShowType", true)
    }

    override fun hasStableIds() = true

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.item_schedule_appwidget)
        val dailySchedule = dailySchedule
        if (dailySchedule == null) {
            remoteView.setTextViewText(R.id.text_schedule_title, "Расписание не найдено")
            remoteView.setViewVisibility(R.id.text_lesson_time, View.GONE)
            remoteView.setViewVisibility(R.id.text_lesson_teachers, View.GONE)
            remoteView.setViewVisibility(R.id.text_lesson_auditoriums, View.GONE)
            return remoteView
        }
        if (dailySchedule.isEmpty()) {
            remoteView.setTextViewText(R.id.text_schedule_title, "Сегодня нет занятий")
            remoteView.setViewVisibility(R.id.text_lesson_time, View.GONE)
            remoteView.setViewVisibility(R.id.text_lesson_teachers, View.GONE)
            remoteView.setViewVisibility(R.id.text_lesson_auditoriums, View.GONE)
            return remoteView
        }

        val lesson = dailySchedule[position]

        setTime(remoteView, lesson)
        setTitle(remoteView, lesson)
        setTeachers(remoteView, lesson)
        setAuditoriums(remoteView, lesson)

        return remoteView
    }

    private fun setTime(view: RemoteViews, lesson: Lesson) {
        if (showStartTime || showEndTime || showOrder || showType) {
            val time = getTime(lesson, showOrder, showStartTime, showEndTime, showType)
            view.setTextViewText(R.id.text_lesson_time, time)
            view.setViewVisibility(R.id.text_lesson_time, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.text_lesson_time, View.GONE)
        }
    }

    private fun setTitle(view: RemoteViews, lesson: Lesson) {
        view.setTextViewText(R.id.text_schedule_title, getTitle(lesson).toString())
    }

    private fun setTeachers(view: RemoteViews, lesson: Lesson) {
        if (showTeachers) {
            view.setTextViewText(R.id.text_lesson_teachers, getTeachers(lesson))
            view.setViewVisibility(R.id.text_lesson_teachers, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.text_lesson_teachers, View.GONE)
        }
    }

    private fun setAuditoriums(view: RemoteViews, lesson: Lesson) {
        if (showAuditoriums) {
            view.setTextViewText(R.id.text_lesson_auditoriums, getAuditoriums(lesson))
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