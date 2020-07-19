package com.mospolytech.mospolyhelper.ui.schedule.lesson_info

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mospolytech.mospolyhelper.MainActivity

import com.mospolytech.mospolyhelper.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.format.DateTimeFormatter

class LessonInfoFragment : DialogFragment() {

    companion object {
        fun newInstance() = LessonInfoFragment()

        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
    }
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM,")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("d MMMM")
    private val viewModel by viewModel<LessonInfoViewModel>()

    private lateinit var lessonTitleTextView: TextView
    private lateinit var lessonTypeTextView: TextView
    private lateinit var lessonTimeTextView: TextView
    private lateinit var lessonAuditoriumsChips: ChipGroup
    private lateinit var teacherChips: ChipGroup
    private lateinit var lessonDateTextView: TextView
    private lateinit var lessonGroupInfoTextView: TextView
    private lateinit var lessonDeadlinesTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_schedule_lesson_info,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lessonTitleTextView = view.findViewById(R.id.text_schedule_title)
        lessonTypeTextView = view.findViewById(R.id.text_schedule_type)
        lessonTimeTextView = view.findViewById(R.id.text_lesson_time)
        lessonAuditoriumsChips = view.findViewById(R.id.chipgroup_lesson_auditoriums)
        teacherChips = view.findViewById(R.id.chipgroup_lesson_teachers)
        lessonDateTextView = view.findViewById(R.id.text_lesson_date)
        lessonGroupInfoTextView = view.findViewById(R.id.text_lesson_group_info)
        lessonDeadlinesTextView = view.findViewById(R.id.text_schedule_deadlines)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        if (toolbar != null) {
            (activity as MainActivity).setSupportActionBar(toolbar)
        }
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar!!.setHomeButtonEnabled(true)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        if (viewModel.lesson.isEmpty) {
            lessonTitleTextView.text = "Нет занятия"
            setTime()
        } else {
            setType()
            setTitle()
            setTime()
            setAuditoriums()
            setTeachers()
            setDate()
            setGroupInfo()
            setDeadlines()
        }

    }

    private fun setType() {
        lessonTypeTextView.setTextColor(if (viewModel.lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
        lessonTypeTextView.text = viewModel.lesson.type.capitalize()
    }

    private fun setTitle() {
        lessonTitleTextView.text = viewModel.lesson.title
    }

    private fun setTime() {
        val (startTime, endTime) = viewModel.lesson.time
        val dateStr = viewModel.date.format(dateFormatter).capitalize()
        lessonTimeTextView.text = "$dateStr с $startTime до $endTime, ${viewModel.lesson.order + 1}-е занятие"
    }

    private fun setAuditoriums() {
        if (viewModel.lesson.auditoriums.isEmpty()) {
            return
        }
        val nightMode = (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        for (auditorium in viewModel.lesson.auditoriums) {
            val audTitle = HtmlCompat.fromHtml(
                auditorium.title.toLowerCase(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            val url = audTitle.getSpans<URLSpan>().firstOrNull()
            val color = convertColorFromString(auditorium.color, nightMode)

            lessonAuditoriumsChips.addView(
                Chip(context).apply {
                    text = if (color != null) {
                        SpannableString(audTitle).apply {
                            setSpan(
                                ForegroundColorSpan(color),
                                0,
                                audTitle.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    } else {
                        audTitle
                    }
                    if (url != null) {
                        setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.url))) }
                    } else {
                        setOnClickListener { Toast.makeText(context, text, Toast.LENGTH_SHORT).show() }
                    }
                }
            )
        }
    }

    private fun convertColorFromString(colorString: String, nightMode: Boolean): Int? {
        if (colorString.isEmpty()) {
            return null
        }
        var color = Color.parseColor(
            if (colorString.length == 4) {
                "#" + colorString[1] + colorString[1] +
                        colorString[2] + colorString[2] +
                        colorString[3] + colorString[3]
            } else {
                colorString
            }
        )
        if (nightMode) {
            color = convertColorToNight(color)
        }
        return color
    }

    private fun convertColorToNight(color: Int): Int {
        val hsv = FloatArray(3)
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv)
        var hue = hsv[0]
        if (hue > 214f && hue < 286f) {
            hue = if (hue >= 250f) 214f else 286f
        }
        hsv[0] = hue
        hsv[2] = hsv[2] * 3
        return Color.HSVToColor(hsv)
    }


    private fun setTeachers() {
        for (teacher in viewModel.lesson.teachers) {
            teacherChips.addView(
                Chip(context).apply {
                    text = teacher.getShortName()
                    setOnClickListener { Toast.makeText(context, teacher.getFullName(), Toast.LENGTH_SHORT).show() }
                }
            )
        }
    }

    private fun setDate() {
        if (viewModel.lesson.dateFrom == viewModel.lesson.dateTo) {
            lessonDateTextView.text = viewModel.lesson.dateFrom.format(shortDateFormatter)
        } else {
            lessonDateTextView.text = "С ${viewModel.lesson.dateFrom.format(shortDateFormatter)} " +
                    "до ${viewModel.lesson.dateTo.format(shortDateFormatter)}"
        }
    }

    private fun setGroupInfo() {
        var text = "Группа ${viewModel.lesson.group.title}, ${viewModel.lesson.group.course}-й курс, длительность семестра: " +
        "с ${viewModel.lesson.group.dateFrom.format(shortDateFormatter)} " +
                "до ${viewModel.lesson.group.dateTo.format(shortDateFormatter)}"
        if (viewModel.lesson.group.isEvening) {
            text += ", вечерняя"
        }
        if (viewModel.lesson.group.comment.isNotEmpty()) {
            text += ", комментарий: " + viewModel.lesson.group.comment
        }
        lessonGroupInfoTextView.text = text
    }

    private fun setDeadlines() {
//        viewModel.deadlinesRepository.foundData.observe(viewLifecycleOwner, Observer { deadlines ->
//            textView.text = deadlines?.joinToString(separator = "\n") { "${it.name} ${it.description}" } ?: "Дедлайнов нет"
//        })
//        viewModel.getSubjectDeadlines(subjectTitle)
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
