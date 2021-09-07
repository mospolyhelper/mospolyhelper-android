package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleLessonInfoBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.utils.description
import com.mospolytech.mospolyhelper.domain.schedule.utils.fullTitle
import com.mospolytech.mospolyhelper.domain.schedule.utils.isOnline
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag.getColor
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonInfoObject
import com.mospolytech.mospolyhelper.features.utils.RoundedBackgroundSpan
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.onSuccess
import com.mospolytech.mospolyhelper.utils.safe
import com.mospolytech.mospolyhelper.utils.show
import kotlinx.coroutines.flow.collect
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
    private val shortDateFormatter = DateTimeFormatter.ofPattern("d MMMM")
    private val dateFormatter2 = DateTimeFormatter.ofPattern("d MMMM yyyy (EE)")

    private val viewModel by viewModel<LessonInfoViewModel>()
    private val viewBinding by viewBinding(FragmentScheduleLessonInfoBinding::bind)
    private val args: LessonInfoFragmentArgs by navArgs()


    override fun getTheme(): Int  = R.style.LessonInfoDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().safe { navigateUp() }
        }

        viewModel.lessonTime = args.lessonTime
        viewModel.lesson = args.lesson
        viewModel.date = LocalDate.ofEpochDay(args.date)
        viewModel.setTags()

        if (viewModel.lesson.isEmpty) {
            //lessonTitleTextView.text = "Нет занятия"
            setTime()
        } else {
            lifecycleScope.launchWhenResumed {
                viewModel.tags.collect {
                    it.onSuccess {
                        setTags(it)
                    }
                }
            }
            setShareButton()
            setType()
            setTitle()
            setTime()
            setAuditoriums()
            setTeachers()
            setDate()
            setGroupInfo()
            setTagButton()
            setDeadlines()
        }

    }

    private fun setShareButton() {
        viewBinding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_schedule_lesson_info_share -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, getShareText(viewModel.date, viewModel.lessonTime, viewModel.lesson))
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    true
                }
                else -> false
            }
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
            textviewLessonTitle.text = viewModel.lesson.title
        }
    }

    private fun setTime() {
        val (startTime, endTime) = viewModel.lessonTime.timeString
        with(viewBinding) {
            this.textLessonTime.text = "$startTime - $endTime (#${viewModel.lessonTime.order + 1})"
        }
    }

    private fun setTags(tags: List<LessonTag>) {
        if (tags.isEmpty()) {
            viewBinding.textviewTags.gone()
        } else {
            viewBinding.textviewTags.show()
        }
        viewBinding.textviewTags.text = getFeaturesString(tags)
    }

    private fun getFeaturesString(tags: List<LessonTag>): SpannableStringBuilder {
        val iterator = tags.iterator()
        val builder = SpannableStringBuilder()
        while (iterator.hasNext()) {
            val tag = iterator.next()
            val color = tag.getColor()
            builder.append(
                "\u00A0",
                RoundedBackgroundSpan(
                    backgroundColor = requireContext().getColor(color.colorId),
                    textColor = requireContext().getColor(color.textColorId),
                    text = tag.title,
                    relativeTextSize = 0.65f
                ),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (iterator.hasNext()) {
                builder.append(" ")
            } else {
                // For bug in lower version android
                builder.append(" ")
            }
        }
        return builder
    }

    private fun setAuditoriums() {
        if (viewModel.lesson.auditoriums.isEmpty()) {
            return
        }
        with(viewBinding) {
            recyclerviewAuditoriums.layoutManager = LinearLayoutManager(context)
            recyclerviewAuditoriums.adapter = LessonInfoObjectAdapter(viewModel.lesson.auditoriums.map {
                object : LessonInfoObject {
                    override val title = it.fullTitle
                    override val description = it.description
                    override val avatar = if (it.isOnline) {
                        R.drawable.ic_fluent_desktop_24_regular
                    } else {
                        R.drawable.ic_fluent_building_24_regular
                    }
                    override val onClickListener: () -> Unit = {
                        if (it.url.isNotEmpty()) {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, it.url)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)

                            AlertDialog.Builder(context)
                                .setTitle("Открыть ссылку?")
                                .setMessage(it.url)
                                .setPositiveButton("Да") { _, _ ->
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(it.url)
                                        )
                                    )
                                }.setNegativeButton("Нет") { _, _ -> }
                                .setNeutralButton("Поделиться") { _, _ -> startActivity(shareIntent) }
                                .create().show()
                        }
                    }
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
                    override val avatar = R.drawable.ic_fluent_hat_graduation_20_regular
                    override val onClickListener: () -> Unit = { }
                }
            })
        }
    }

    private fun setDate() {
        with(viewBinding) {
            textLessonDate.text = viewModel.date.format(dateFormatter2)
            if (viewModel.lesson.dateFrom == viewModel.lesson.dateTo) {
                textLessonDates.text = viewModel.lesson.dateFrom.format(shortDateFormatter)
            } else {
                textLessonDates.text = "${viewModel.lesson.dateFrom.format(shortDateFormatter)} " +
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
                    override val avatar = R.drawable.ic_fluent_people_20_regular
                    override val onClickListener: () -> Unit = {
//                        findNavController().safe {
//                            navigate(
//                                LessonInfoFragmentDirections.actionLessonInfoFragmentToGroupInfoFragment(it.title)
//                            )
//                        }
                    }
                }
            })
        }
    }

    private fun setTagButton() {
        viewBinding.buttonAddLabel.setOnClickListener {
            findNavController().safe { navigate(
                LessonInfoFragmentDirections
                    .actionLessonInfoFragmentToLessonTagFragment(
                        lesson = viewModel.lesson,
                        dayOfWeek = viewModel.date.dayOfWeek.value,
                        order = viewModel.lessonTime.order
                    )
            ) }
        }
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

    private fun getShareText(date: LocalDate, lessonTime: LessonTime, lesson: Lesson): String {
        val res = StringBuilder()

        res.append(date.format(shortDateFormatter))

        val (startTime, endTime) = lessonTime.timeString
        val time = ", $startTime - $endTime\n"
        res.append(time)

        res.append(lesson.title)
        if (lesson.teachers.isNotEmpty()) {
            res.append("\n")
            res.append("🎓  ")
            res.append(lesson.teachers.joinToString { it.name })
        }

        if (lesson.groups.isNotEmpty()) {
            res.append("\n")
            res.append("👥  ")
            res.append(lesson.groups.joinToString { it.title })
        }

        if (lesson.auditoriums.isNotEmpty()) {
            res.append("\n")
            res.append("🏛️  ")
            res.append(lesson.auditoriums.joinToString { it.title })

            val urls = lesson.auditoriums.filter { it.url.isNotEmpty() }
            if (urls.isNotEmpty()) {
                res.append("\n")
                res.append(urls.joinToString(separator = "\n") { it.url })
            }
        }
        return res.toString()
    }
}
