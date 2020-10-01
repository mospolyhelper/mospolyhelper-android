package com.mospolytech.mospolyhelper.features.ui.settings.schedule_appwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.Auditorium
import com.mospolytech.mospolyhelper.domain.schedule.model.Group
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.Teacher
import com.mospolytech.mospolyhelper.features.appwidget.schedule.LessonRemoteAdapter
import com.mospolytech.mospolyhelper.features.appwidget.schedule.ScheduleAppWidgetProvider
import com.mospolytech.mospolyhelper.utils.Action0
import com.mospolytech.mospolyhelper.utils.DefaultSettings
import com.mospolytech.mospolyhelper.utils.Event0
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import java.lang.StringBuilder
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

//    app:key="ScheduleAppwidgetShowOrder"
//    app:key="ScheduleAppwidgetShowStartTime"
//    app:key="ScheduleAppwidgetShowEndTime"
//    app:key="ScheduleAppwidgetShowType"
//    app:key="ScheduleAppwidgetShowAuditoriums"
//    app:key="ScheduleAppwidgetShowTeachers"

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            "ScheduleAppwidgetShowOrder",
            "ScheduleAppwidgetShowStartTime",
            "ScheduleAppwidgetShowEndTime",
            "ScheduleAppwidgetShowType",
            "ScheduleAppwidgetShowAuditoriums",
            "ScheduleAppwidgetShowTeachers" -> {
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

    private var showAuditoriums = true
    private var showEndTime = true
    private var showOrder = true
    private var showType = true
    private var showStartTime = true
    private var showTeachers = true

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

        showOrder = prefs.getBoolean("ScheduleAppwidgetShowOrder", true)
        showStartTime = prefs.getBoolean("ScheduleAppwidgetShowStartTime", true)
        showEndTime = prefs.getBoolean("ScheduleAppwidgetShowEndTime", true)
        showAuditoriums = prefs.getBoolean("ScheduleAppwidgetShowAuditoriums", true)
        showTeachers = prefs.getBoolean("ScheduleAppwidgetShowTeachers", true)
        showType = prefs.getBoolean("ScheduleAppwidgetShowType", true)

        var idFull = prefs.getString(
            PreferenceKeys.ScheduleGroupTitle,
            DefaultSettings.ScheduleGroupTitle
        )!!
        val isStudent = prefs.getBoolean(
            PreferenceKeys.ScheduleUserTypePreference,
            DefaultSettings.ScheduleUserTypePreference
        )
        if (!isStudent) {
            idFull = "преп. ID" + idFull
        }
        val date = LocalDate.now().format(dateFormatter).capitalize()

        appWidgetTitle.text = "$date | $idFull"
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

        private val teacher1 = Teacher(listOf("Арсентьев", "Дмитрий", "Андреевич"))
        private val teacher2 = Teacher(listOf("Норин", "Владимир", "Павлович"))
        private val teacher3 = Teacher(listOf("Петров", "Денис", "Альбертович"))

        private val auditorium1 = Auditorium("Пр ВЦ 3 (2555)", "")
        private val auditorium2 = Auditorium("Пк214", "")
        private val auditorium3 = Auditorium("В165", "")

        private val lessons = listOf(
            Lesson(
                2,
                "Информационные процессы и обработка в принтмедиа",
                listOf(teacher1, teacher3),
                LocalDate.MIN,
                LocalDate.MAX,
                listOf(auditorium1),
                "Лаб. работа",
                listOf()
            ),
            Lesson(
                3,
                "Математика",
                listOf(teacher2),
                LocalDate.MIN,
                LocalDate.MAX,
                listOf(auditorium2, auditorium1),
                "Курсовой проект",
                listOf()
            ),
            Lesson(
                4,
                "Основы алгоритмизации и программирования",
                listOf(teacher1),
                LocalDate.MIN,
                LocalDate.MAX,
                listOf(auditorium3),
                "Лекция",
                listOf()
            )
        )

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
            setAuditoriums(view, lesson)

            return view
        }

        //override fun isEnabled(position: Int) = false

        private fun setTime(view: View, lesson: Lesson) {
            val timeTextView = view.findViewById<TextView>(R.id.text_lesson_time)
            if (showStartTime || showEndTime || showOrder || showType) {
                val time = LessonRemoteAdapter.getTime(lesson, showOrder, showStartTime, showEndTime, showType)
                timeTextView.text = time
                timeTextView.visibility = View.VISIBLE
            } else {
                timeTextView.visibility = View.GONE
            }
        }

        private fun setTitle(view: View, lesson: Lesson) {
            view.findViewById<TextView>(R.id.text_schedule_title).text = LessonRemoteAdapter.getTitle(lesson)
        }

        private fun setTeachers(view: View, lesson: Lesson) {
            val teachersTextView = view.findViewById<TextView>(R.id.text_lesson_teachers)
            if (showTeachers) {
                teachersTextView.text = LessonRemoteAdapter.getTeachers(lesson)
                teachersTextView.visibility = View.VISIBLE
            } else {
                teachersTextView.visibility = View.GONE
            }
        }

        private fun setAuditoriums(view: View, lesson: Lesson) {
            val auditoriumsTextView = view.findViewById<TextView>(R.id.text_lesson_auditoriums)
            if (showAuditoriums) {
                auditoriumsTextView.text = LessonRemoteAdapter.getAuditoriums(lesson)
                auditoriumsTextView.visibility = View.VISIBLE
            } else {
                auditoriumsTextView.visibility = View.GONE
            }
        }
    }
}