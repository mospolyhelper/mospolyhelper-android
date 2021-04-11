package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info

import android.app.AlertDialog
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
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import by.kirich1409.viewbindingdelegate.viewBinding

import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleLessonInfoBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.Group
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonInfoObject
import com.mospolytech.mospolyhelper.domain.schedule.utils.description
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleFragmentDirections
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.android.synthetic.main.fragment_schedule_lesson_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LessonInfoFragment : DialogFragment(R.layout.fragment_schedule_lesson_info) {

    companion object {
        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
    }
    private val lessonLabelOneDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM")
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EE, d MMM,")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("d MMMM")
    private val viewModel by viewModel<LessonInfoViewModel>()

    private val viewBinding by viewBinding(FragmentScheduleLessonInfoBinding::bind)


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar!!.setHomeButtonEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        if (viewModel.lesson.isEmpty) {
            //lessonTitleTextView.text = "Нет занятия"
            setTime()
        } else {
            setType()
            setTitle()
            setTime()
            setAuditoriums()
            setTeachers()
            setDate()
            setGroupInfo()
            setLabels()
            setDeadlines()
        }

    }

    private fun setType() {
        with(viewBinding) {
            textScheduleType.setTextColor(if (viewModel.lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
            textScheduleType.text = viewModel.lesson.type
        }
    }

    private fun setTitle() {
        with(viewBinding) {
            textScheduleTitle.text = viewModel.lesson.title
        }
    }

    private fun setTime() {
        val (startTime, endTime) = viewModel.lesson.time
        val dateStr = viewModel.date.format(dateFormatter).capitalize()
        with(viewBinding) {
            this.textLessonTime.text = "$dateStr $startTime - $endTime" + ", ${viewModel.lesson.order + 1}-е занятие"
        }
    }

    private fun setAuditoriums() {
        if (viewModel.lesson.auditoriums.isEmpty()) {
            return
        }
        val nightMode = (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        for (auditorium in viewModel.lesson.auditoriums) {
            val audTitle = HtmlCompat.fromHtml(
                auditorium.title,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            val url = audTitle.getSpans<URLSpan>().firstOrNull()
            val color = convertColorFromString(auditorium.color, nightMode)

            val text = if (color != null) {
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

            val onClickListener = if (url != null) {
                { it: View ->
                    AlertDialog.Builder(context).setTitle("Открыть ссылку?")
                        .setPositiveButton("Да") { _, _ ->
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.url)))
                        }.setNegativeButton("Нет") { _, _ -> }.create().show()

                }
            } else {
                {  it: View ->
                    Toast.makeText(context, text.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
        with(viewBinding) {
            recyclerviewAuditoriums.layoutManager = LinearLayoutManager(context)
            recyclerviewAuditoriums.adapter = LessonInfoObjectAdapter(viewModel.lesson.auditoriums.map {
                object : LessonInfoObject {
                    override val title = HtmlCompat.fromHtml(
                        it.title,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString()
                    override val description = it.description
                    override val avatar = R.drawable.ic_baseline_apartment_24

                }
            })
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
        with(viewBinding) {
            recyclerviewTeachers.layoutManager = LinearLayoutManager(context)
            recyclerviewTeachers.adapter = LessonInfoObjectAdapter(viewModel.lesson.teachers.map {
                object : LessonInfoObject {
                    override val title = it.name
                    override val description = ""
                    override val avatar = R.drawable.ic_round_person_24

                }
            })
        }
    }

    private fun setDate() {
        with(viewBinding) {
                    if (viewModel.lesson.dateFrom == viewModel.lesson.dateTo) {
            textLessonDate.text = viewModel.lesson.dateFrom.format(shortDateFormatter)
        } else {
            textLessonDate.text = "${viewModel.lesson.dateFrom.format(shortDateFormatter)} " +
                    "- ${viewModel.lesson.dateTo.format(shortDateFormatter)}"
        }
        }

    }

    private fun setGroupInfo() {
        with(viewBinding) {
            recyclerviewGroups.layoutManager = LinearLayoutManager(context)
            recyclerviewGroups.adapter = LessonInfoObjectAdapter(viewModel.lesson.groups.map {
                object : LessonInfoObject {
                    override val title = it.title
                    override val description = if (it.isEvening) "Вечерняя" else ""
                    override val avatar = R.drawable.ic_group_24
                }
            }) {
                findNavController().safe {
                    navigate(
                        LessonInfoFragmentDirections.actionLessonInfoFragmentToGroupInfoFragment(it)
                    )
                }
            }
        }
    }

    private fun createChip(text: CharSequence, root: ViewGroup, onClickListener: View.OnClickListener): Chip {
        val chip = layoutInflater.inflate(R.layout.chip_schedule_lesson_info, null, false) as Chip
        chip.text = text
        chip.setOnClickListener(onClickListener)
        return chip
    }

    private fun setLabels() {
        //lessonLabelOneDateTextView.text = "Только на ${viewModel.date.format(lessonLabelOneDateFormatter)}"

    }

    private fun setDeadlines() {
//        lessonDeadlinesTextView.setOnClickListener {
//            findNavController().safe { navigate(NavGraphDirections.navDeadlines()) }
//        }
//        viewModel.deadlinesRepository.foundData.observe(viewLifecycleOwner, Observer { deadlines ->
//            textView.text = deadlines?.joinToString(separator = "\n") { "${it.name} ${it.description}" } ?: "Дедлайнов нет"
//        })
//        viewModel.getSubjectDeadlines(subjectTitle)
    }
}
