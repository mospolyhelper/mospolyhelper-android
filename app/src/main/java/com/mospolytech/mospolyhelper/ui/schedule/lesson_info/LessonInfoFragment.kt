package com.mospolytech.mospolyhelper.ui.schedule.lesson_info

import android.content.res.Configuration
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.transition.Slide
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import com.mospolytech.mospolyhelper.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.models.schedule.Group
import com.mospolytech.mospolyhelper.repository.models.schedule.Lesson
import java.text.SimpleDateFormat
import java.util.*

class LessonInfoFragment : Fragment() {

    companion object {
        fun newInstance() = LessonInfoFragment()

        val lessonTypeColors = listOf(
            0xeb4141,   // Exam, Credit,..
            0x29b6f6    // Other
        )
    }
    private val viewModel by viewModels<LessonInfoViewModel>()

    var isNoteEdited: Boolean = false


    init {
        enterTransition = Slide(Gravity.RIGHT)
        exitTransition = Slide(Gravity.LEFT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lesson_info, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        if (toolbar != null) {
            (activity as MainActivity).setSupportActionBar(toolbar)
        }
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        if (viewModel.lesson.isEmpty) {
            view.findViewById<TextView>(R.id.text_schedule_title).text = "Нет занятия"
            setTime(view.findViewById<TextView>(R.id.text_schedule_time), viewModel.lesson, viewModel.date)
        } else {
            setType(view.findViewById<TextView>(R.id.text_schedule_type), viewModel.lesson)
            setTitle(view.findViewById<TextView>(R.id.text_schedule_title), viewModel.lesson)
            setTime(view.findViewById<TextView>(R.id.text_schedule_time), viewModel.lesson, viewModel.date)
            setAuditoriums(view.findViewById<TextView>(R.id.text_schedule_auditoriums), viewModel.lesson)
            setTeachers(view.findViewById<TextView>(R.id.text_schedule_teachers), viewModel.lesson)
            setDate(view.findViewById<TextView>(R.id.text_schedule_date), viewModel.lesson)
            setGroupInfo(view.findViewById<TextView>(R.id.text_schedule_group), viewModel.lesson.group)
        }

        return view
    }

    fun setType(textView: TextView, lesson: Lesson) {
        textView.setTextColor(if (lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
        textView.text = lesson.type.toUpperCase()
    }

    fun setTitle(textView: TextView, lesson: Lesson) {
        textView.text = lesson.title
    }

    fun setTime(textView: TextView, lesson: Lesson, date: Calendar) {
        val (startTime, endTime) = lesson.time
        val dateStr = SimpleDateFormat("dddd, d MMMM,", Locale.getDefault()).format(date)
        // TODO: val dateStr = dateStr[0].toUpperCase() + dateStr.Substring(1)
        textView.text = "$dateStr с $startTime до $endTime, ${lesson.order + 1}-е занятие"
    }

    fun setAuditoriums(textView: TextView, lesson: Lesson) {
        val auditoriums = SpannableStringBuilder()
        if (lesson.auditoriums.isEmpty()) {
            textView.setText("", TextView.BufferType.NORMAL)
            return
        }
        val nightMode = (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        for (i in 0 until lesson.auditoriums.size - 1) {
            val audTitle = HtmlCompat.fromHtml(lesson.auditoriums[i].title.toLowerCase(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            if (lesson.auditoriums[i].color.isNotEmpty()) {
                var colorString = lesson.auditoriums[i].color
                if (colorString.length == 4) {
                    colorString = "#" +
                            colorString[1] + colorString[1] +
                            colorString[2] + colorString[2] +
                            colorString[3] + colorString[3]
                }
                var color = Color.parseColor(colorString)
                if (nightMode) {
                    val hsv = FloatArray(3)
                    Color.RGBToHSV(Color.red(color), Color.red(color), Color.red(color), hsv)
                    var hue = hsv[0]
                    if (hue > 214f && hue < 286f) {
                        hue = if (hue >= 250f) 214f else 286f
                    }
                    hsv[0] = hue
                    hsv[2] = hsv[2] * 3
                    color = Color.HSVToColor(hsv)
                }
                auditoriums
                    .append(audTitle,
                        ForegroundColorSpan(color),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    ).append(", ",
                        ForegroundColorSpan(color),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            } else {
                auditoriums
                    .append(audTitle)
                    .append(", ")
            }
        }
        if (lesson.auditoriums.isNotEmpty()) {
            val audTitle = HtmlCompat.fromHtml(lesson.auditoriums[lesson.auditoriums.lastIndex].title.toLowerCase(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            if (lesson.auditoriums[lesson.auditoriums.lastIndex].color.isNotEmpty()) {
                var colorString = lesson.auditoriums[lesson.auditoriums.lastIndex].color
                if (colorString.length == 4) {
                    colorString = "#" +
                            colorString[1] + colorString[1] +
                            colorString[2] + colorString[2] +
                            colorString[3] + colorString[3];
                }
                var color = Color.parseColor(colorString)
                if (nightMode) {
                    val hsv = FloatArray(3)
                    Color.RGBToHSV(Color.red(color), Color.red(color), Color.red(color), hsv)
                    var hue = hsv[0]
                    if (hue > 214f && hue < 286f) {
                        hue = if (hue >= 250f) 214f else 286f
                    }
                    hsv[0] = hue
                    hsv[2] = hsv[2] * 3
                    color = Color.HSVToColor(hsv)
                }
                auditoriums.append(audTitle,
                    ForegroundColorSpan(color),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                auditoriums.append(audTitle)
            }
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(auditoriums, TextView.BufferType.NORMAL)
    }


    fun setTeachers(textView: TextView, lesson: Lesson) {
        textView.text = lesson.teachers.joinToString { it.getFullName() }
    }

    fun setDate(textView: TextView, lesson: Lesson) {
        if (lesson.dateFrom == lesson.dateTo) {
            textView.text = SimpleDateFormat("d MMMM", Locale.getDefault()).format(lesson.dateFrom)
        } else {
            textView.text = "С ${SimpleDateFormat("d MMMM ", Locale.getDefault()).format(lesson.dateFrom)}" +
                    "до ${SimpleDateFormat("d MMMM", Locale.getDefault()).format(lesson.dateTo)}"
        }
    }

    fun setGroupInfo(textView: TextView, group: Group) {
        var text = "Группа ${group.title}, ${group.course}-й курс, длительность семестра: " +
        "с ${SimpleDateFormat("d MMMM", Locale.getDefault()).format(group.dateFrom)} " +
                "до ${SimpleDateFormat("d MMMM", Locale.getDefault()).format(group.dateTo)}"
        if (group.isEvening) {
            text += ", вечерняя"
        }
        if (group.comment.isNotEmpty()) {
            text += ", комментарий: " + group.comment
        }
        textView.text = text
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        // TODO: Use the ViewModel
    }

    override fun onStop() {
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        super.onStop()
    }
}
