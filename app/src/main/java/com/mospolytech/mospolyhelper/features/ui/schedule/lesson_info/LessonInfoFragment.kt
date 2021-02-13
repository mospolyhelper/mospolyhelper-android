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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.NavGraphDirections

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.Group
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.android.synthetic.main.fragment_schedule_lesson_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LessonInfoFragment : DialogFragment() {

    companion object {
        fun newInstance() = LessonInfoFragment()

        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
    }
    private val lessonLabelOneDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM")
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EE, d MMM,")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("d MMM")
    private val viewModel by viewModel<LessonInfoViewModel>()

    private val args: LessonInfoFragmentArgs by navArgs()

    private lateinit var lessonTitleTextView: TextView
    private lateinit var lessonTypeTextView: TextView
    private lateinit var lessonTimeTextView: TextView
    private lateinit var lessonAuditoriumsChips: ChipGroup
    private lateinit var teacherChips: ChipGroup
    private lateinit var lessonDateTextView: TextView
    private lateinit var lessonGroupsChipGroup: ChipGroup
    private lateinit var lessonLabelsChipGroup: ChipGroup
    private lateinit var lessonDeadlinesTextView: TextView
    private lateinit var lessonLabelOneDateTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

//        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
//        sharedElementReturnTransition = sharedElementEnterTransition
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
        lessonGroupsChipGroup = view.findViewById(R.id.chipgroup_lesson_groups)
        lessonDeadlinesTextView = view.findViewById(R.id.text_schedule_deadlines)
        lessonLabelsChipGroup = view.findViewById(R.id.chipgroup_lesson_labels)
        lessonLabelOneDateTextView = view.findViewById(R.id.text_label_one_date)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar!!.setHomeButtonEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

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
            setLabels()
            setDeadlines()
        }

    }

    private fun setType() {
        lessonTypeTextView.setTextColor(if (viewModel.lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
        lessonTypeTextView.text = viewModel.lesson.type + ", ${viewModel.lesson.order + 1}-е занятие"
    }

    private fun setTitle() {
        lessonTitleTextView.text = viewModel.lesson.title
    }

    private fun setTime() {
        val (startTime, endTime) = viewModel.lesson.time
        val dateStr = viewModel.date.format(dateFormatter).capitalize()
        lessonTimeTextView.text = "$dateStr $startTime - $endTime"
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

            lessonAuditoriumsChips.addView(
                createChip(text, lessonAuditoriumsChips, onClickListener)
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
            val text = if (viewModel.lesson.teachers.size < 3) {
                teacher.name
            } else {
                teacher.getShortName()
            }
            teacherChips.addView(
                createChip(text, teacherChips) {
                    Toast.makeText(context, teacher.name, Toast.LENGTH_SHORT).show()
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
        for (group in viewModel.lesson.groups) {
            lessonGroupsChipGroup.addView(
                createChip(group.title, lessonGroupsChipGroup) {
                    val text = "${group.title}"
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                }
            )
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
