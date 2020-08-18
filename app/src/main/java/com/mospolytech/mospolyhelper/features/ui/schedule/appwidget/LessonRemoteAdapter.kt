package com.mospolytech.mospolyhelper.features.ui.schedule.appwidget

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
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleLocalConverter
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.utils.DefaultSettings
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import java.lang.StringBuilder
import java.time.LocalDate


class LessonRemoteAdapter(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {
    companion object {
        val lessonTypeColors = listOf(
            0xffe74c3c.toInt(),   // Exam, Credit,..
            0xff2e86c1.toInt()    // Other
        )
    }

    private var dailySchedule: List<Lesson> = listOf()

    private var showStartTime = true
    private var showEndTime = false
    private var showOrder = false
    private var showTeachers = false
    private var showAuditoriums = true

    override fun onCreate() {
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onDataSetChanged() {
        val repos =
            ScheduleLocalDataSource(
                ScheduleLocalConverter()
            )
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val groupTitle = prefs.getString(
            PreferenceKeys.ScheduleGroupTitle,
            DefaultSettings.ScheduleGroupTitle
        )!!
        val schedule = repos.get(groupTitle, true)
        if (schedule == null) {
            dailySchedule = listOf()
            return
        }
        val date = LocalDate.of(2020, 6, 29)
        dailySchedule = schedule?.getSchedule(date, Schedule.Filter.default)
    }

    override fun hasStableIds() = true

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.item_schedule_appwidget)
        val lesson = dailySchedule[position]

        setTime(remoteView, lesson)
        setTitle(remoteView, lesson)
        setTeachers(remoteView, lesson)
        setAuditoriums(remoteView, lesson)

        return remoteView
    }

    private fun setTime(view: RemoteViews, lesson: Lesson) {
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
            }
            res.append(timeEnd)
        }
        builder.append(res)
        builder.append("  ")
        builder.appendAny(
            "\u00A0\u00A0${lesson.type.toLowerCase().replace(' ', '\u00A0')}\u00A0\u00A0",
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
            ForegroundColorSpan(0xffffffff.toInt()),
            BackgroundColorSpan(color2)
        )
        if (showStartTime || showEndTime || showOrder) {
            view.setTextViewText(R.id.text_lesson_time, builder)
            view.setViewVisibility(R.id.text_lesson_time, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.text_lesson_time, View.GONE)
        }
    }
    private fun SpannableStringBuilder.appendAny(text: String, flags: Int, vararg spans: Any) {
        val start = length
        append(text)
        val length = length
        for (span in spans) {
            setSpan(span, start, length, flags)
        }
    }

    // TODO Remove in release
    private fun processTitle(rawTitle: String): String {
        val regex1 = Regex("""(\p{L}|\))\(""")
        val regex2 = Regex("""\)(\p{L}|\()""")
        val regex3 = Regex("""(\p{L})-(\p{L})""")

        return rawTitle
            .trim()
            .replace(regex1, "\$1 (")
            .replace(regex2, ") \$1")
            .replace(regex3, "\$1\u200b-\u200b\$2")
            .capitalize()
    }

    private fun setTitle(view: RemoteViews, lesson: Lesson) {
        view.setTextViewText(R.id.text_schedule_title, processTitle(lesson.title))
    }

    private fun setTeachers(view: RemoteViews, lesson: Lesson) {
        if (showTeachers) {
            view.setTextViewText(R.id.text_lesson_teachers, lesson.teachers.joinToString { it.getShortName() })
            view.setViewVisibility(R.id.text_lesson_teachers, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.text_lesson_teachers, View.GONE)
        }
    }

    private fun setAuditoriums(view: RemoteViews, lesson: Lesson) {
        if (showAuditoriums) {
            view.setTextViewText(R.id.text_lesson_auditoriums, lesson.auditoriums.joinToString { parseAuditoriumTitle(it.title) })
            view.setViewVisibility(R.id.text_lesson_auditoriums, View.VISIBLE)
        } else {
            view.setViewVisibility(R.id.text_lesson_auditoriums, View.GONE)
        }
    }

    private fun parseAuditoriumTitle(title: String): String {
        val str = SpannableString(
            HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
        ).toString()
        val index = str.indexOfFirst { it.isLetterOrDigit() }
        return if (index == -1) str else str.substring(index)
    }

    override fun getCount() = dailySchedule.size

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
    }
}