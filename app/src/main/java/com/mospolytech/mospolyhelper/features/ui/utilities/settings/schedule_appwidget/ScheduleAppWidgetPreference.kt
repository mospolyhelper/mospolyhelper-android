package com.mospolytech.mospolyhelper.features.ui.utilities.settings.schedule_appwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.AuditoriumSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.auditorium.Auditorium
import com.mospolytech.mospolyhelper.domain.schedule.model.group.Group
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonPlace
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.model.teacher.Teacher
import com.mospolytech.mospolyhelper.features.appwidget.schedule.LessonRemoteAdapter
import com.mospolytech.mospolyhelper.features.appwidget.schedule.ScheduleAppWidgetProvider
import com.mospolytech.mospolyhelper.utils.Action0
import com.mospolytech.mospolyhelper.utils.Event0
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleAppWidgetPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {
    companion object {
        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d MMM")
    }

    private val prefs: SharedPreferences

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            "ScheduleAppwidgetShowOrder",
            "ScheduleAppwidgetShowStartTime",
            "ScheduleAppwidgetShowEndTime",
            "ScheduleAppwidgetShowType",
            "ScheduleAppwidgetShowTeachers",
            "ScheduleAppwidgetShowGroups",
            "ScheduleAppwidgetShowAuditoriums",
             -> {
                (update as Action0).invoke()
            }
        }
    }

    init {
        layoutResource = R.layout.preference_schedule_appwidget
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    private val update: Event0 = Action0()

    private var showStartTime = true
    private var showEndTime = false
    private var showOrder = false
    private var showType = true
    private var showTeachers = false
    private var showGroups = false
    private var showAuditoriums = true

    private lateinit var appWidgetTitle: TextView
    private lateinit var appWidgetList: ListView

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        with(holder.itemView) {

            appWidgetTitle = findViewById(R.id.text_lesson_date)
            appWidgetList = findViewById(R.id.list_schedule)

            buildAppWidget(this)
            update += ::updateAppWidget
        }
    }

    private fun updateAppWidget() {

        showOrder = prefs.getBoolean("ScheduleAppwidgetShowOrder", false)
        showStartTime = prefs.getBoolean("ScheduleAppwidgetShowStartTime", true)
        showEndTime = prefs.getBoolean("ScheduleAppwidgetShowEndTime", false)
        showAuditoriums = prefs.getBoolean("ScheduleAppwidgetShowAuditoriums", true)
        showTeachers = prefs.getBoolean("ScheduleAppwidgetShowTeachers", false)
        showGroups = prefs.getBoolean("ScheduleAppwidgetShowGroups", false)
        showType = prefs.getBoolean("ScheduleAppwidgetShowType", true)

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

        appWidgetTitle.text = "$date | $userTitle"
        appWidgetList.adapter = Adapter(context)

        val intent = Intent(context, ScheduleAppWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, ScheduleAppWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun buildAppWidget(view: View) {
        view.isClickable = false
        updateAppWidget()
        appWidgetList.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                view.parent.requestDisallowInterceptTouchEvent(true)
            }
            false
        }
    }

    inner class Adapter(context: Context): BaseAdapter() {
        private val inflater = LayoutInflater.from(context)

        private val teacher1 = Teacher("Арсентьев Дмитрий Андреевич")
        private val teacher2 = Teacher("Норин Владимир Павлович")
        private val teacher3 = Teacher("Петров Денис Альбертович")

        private val group1 = Group("181-721", false)
        private val group2 = Group("181-722", false)
        private val group3 = Group("181-725", false)

        private val auditorium1 = Auditorium("Пр ВЦ 3 (2555)", "", "", "")
        private val auditorium2 = Auditorium("Пк214", "", "", "")
        private val auditorium3 = Auditorium("В165", "", "", "")

        private val lessons = listOf(
            LessonPlace(
                listOf(
                    Lesson(
                        "Информационные процессы и обработка в принтмедиа",
                        "Лаб. работа",
                        listOf(teacher1, teacher3),
                        listOf(auditorium1),
                        listOf(group1),
                        LocalDate.MIN,
                        LocalDate.MAX
                    )
                ),
                LessonTime(2, false)
            ),
            LessonPlace(
                listOf(
                    Lesson(
                        "Математика",
                        "Курсовой проект",
                        listOf(teacher2),
                        listOf(auditorium2, auditorium1),
                        listOf(group2),
                        LocalDate.MIN,
                        LocalDate.MAX
                    )
                ),
                LessonTime(3, false)
            ),
            LessonPlace(
                listOf(
                    Lesson(
                        "Основы алгоритмизации и программирования",
                        "Лекция",
                        listOf(teacher1),
                        listOf(auditorium3),
                        listOf(group1, group2, group3),
                        LocalDate.MIN,
                        LocalDate.MAX
                    )
                ),
                LessonTime(4, false)
            )
        ).flatMap { lessonPlace ->
            lessonPlace.lessons.map { Pair(it, lessonPlace.time) }
        }

        override fun getCount() = lessons.size

        override fun getItem(position: Int) = lessons[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView
                ?: inflater.inflate(R.layout.item_schedule_appwidget, parent, false)

            val lesson = lessons[position]

            setTime(view, lesson)
            setTitle(view, lesson)
            setTeachers(view, lesson)
            setGroups(view, lesson)
            setAuditoriums(view, lesson)

            return view
        }

        //override fun isEnabled(position: Int) = false

        private fun setTime(view: View, lesson: Pair<Lesson, LessonTime>) {
            val timeTextView = view.findViewById<TextView>(R.id.text_lesson_time)
            if (showStartTime || showEndTime || showOrder || showType) {
                val time = LessonRemoteAdapter.getTime(
                    lesson.first,
                    lesson.second.timeString,
                    lesson.second.order,
                    showOrder,
                    showStartTime,
                    showEndTime,
                    showType
                )
                timeTextView.text = time
                timeTextView.visibility = View.VISIBLE
            } else {
                timeTextView.visibility = View.GONE
            }
        }

        private fun setTitle(view: View, lesson: Pair<Lesson, LessonTime>) {
            view.findViewById<TextView>(R.id.textview_lesson_title).text = LessonRemoteAdapter.getTitle(lesson.first)
        }

        private fun setTeachers(view: View, lesson: Pair<Lesson, LessonTime>) {
            val teachersTextView = view.findViewById<TextView>(R.id.text_lesson_teachers)
            if (showTeachers) {
                teachersTextView.text = LessonRemoteAdapter.getTeachers(lesson.first)
                teachersTextView.visibility = View.VISIBLE
            } else {
                teachersTextView.visibility = View.GONE
            }
        }

        private fun setGroups(view: View, lesson: Pair<Lesson, LessonTime>) {
            val groupsTextView = view.findViewById<TextView>(R.id.textview_groups)
            if (showGroups) {
                groupsTextView.text = LessonRemoteAdapter.getGroups(lesson.first)
                groupsTextView.visibility = View.VISIBLE
            } else {
                groupsTextView.visibility = View.GONE
            }
        }

        private fun setAuditoriums(view: View, lesson: Pair<Lesson, LessonTime>) {
            val auditoriumsTextView = view.findViewById<TextView>(R.id.text_lesson_auditoriums)
            if (showAuditoriums) {
                auditoriumsTextView.text = LessonRemoteAdapter.getAuditoriums(lesson.first)
                auditoriumsTextView.visibility = View.VISIBLE
            } else {
                auditoriumsTextView.visibility = View.GONE
            }
        }
    }
}