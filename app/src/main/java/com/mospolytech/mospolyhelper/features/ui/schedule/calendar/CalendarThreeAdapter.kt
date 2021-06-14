package com.mospolytech.mospolyhelper.features.ui.schedule.calendar

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemScheduleCalendarThreeBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonPlace
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import com.mospolytech.mospolyhelper.domain.schedule.utils.cutTitle
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonFeaturesSettings
import com.mospolytech.mospolyhelper.features.utils.getAttributeColor
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Event1
import com.mospolytech.mospolyhelper.utils.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// TODO: Return filter
class CalendarThreeAdapter(
    val schedule: Schedule?,
    val lessonFeaturesSettings: LessonFeaturesSettings
) : RecyclerView.Adapter<CalendarThreeAdapter.ViewHolder>() {
    companion object {
        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
    }
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d MMMM")
    val firstPosDate: LocalDate = schedule?.dateFrom ?: LocalDate.MIN
    private val itemCount = schedule?.let {
        it.dateFrom.until(schedule.dateTo, ChronoUnit.DAYS).toInt() + 1
    } ?: 0

    val dayClick: Event1<LocalDate> = Action1()

    override fun getItemCount() = itemCount

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_schedule_calendar_three, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val date = firstPosDate.plusDays(position.toLong())
        val dailySchedule = schedule?.getLessons(date) ?: emptyList()
        viewHolder.bind(dailySchedule, date)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val colorTitle: Int = getAttributeColor(R.attr.colorOnPrimarySurfaceSecondary)!!
        private val colorCurrentTitle: Int = view.context.getColor(R.color.calendarCurrentTitle)

        private val viewBinding by viewBinding(ItemScheduleCalendarThreeBinding::bind)


        init {
            viewBinding.linearLayoutScheduleGrid.setOnClickListener {
                (dayClick as Action1).invoke(firstPosDate.plusDays(bindingAdapterPosition.toLong()))
            }
        }

        fun bind(dailySchedule: List<LessonPlace>, date: LocalDate) {
            setLessons(dailySchedule)
            setHead(date)
            setPadding(date)
        }

        private fun setPadding(date: LocalDate) {
            val dp4 = 8.dp(itemView.context).toInt()

            var paddingEnd = dp4
            var paddingBottom = dp4

            when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> {
                    paddingEnd = 0
                    paddingBottom = 0
                }
                DayOfWeek.TUESDAY -> {
                    paddingEnd = 0
                    paddingBottom = 0
                }
                DayOfWeek.WEDNESDAY -> {
                    paddingEnd = 0
                    paddingBottom = 0
                }
                DayOfWeek.THURSDAY -> {
                    paddingEnd = 0
                    paddingBottom = 0
                }
                DayOfWeek.FRIDAY -> {
                    paddingEnd = 0
                }
                DayOfWeek.SATURDAY -> {
                    paddingEnd = 0
                }
                DayOfWeek.SUNDAY -> {
                    if (bindingAdapterPosition % 3 == 2) paddingEnd = 0
                }
            }
            viewBinding.linearLayoutScheduleGrid.setPaddingRelative(0, 0, paddingEnd, paddingBottom)
        }

        private fun setHead(date: LocalDate) {

            val today = LocalDate.now()

            if (date == today) {
                viewBinding.textScheduleTimeGrid.setTextColor(colorCurrentTitle)
            } else {
                viewBinding.textScheduleTimeGrid.setTextColor(colorTitle)
            }
            viewBinding.textScheduleTimeGrid.setText(date.format(dateFormatter), TextView.BufferType.NORMAL)
        }

        private fun setLessons(dailySchedule: List<LessonPlace>) {
            val res = SpannableStringBuilder()

            if (dailySchedule.isNotEmpty()) {
                var title: String
                var time: LessonTimeUtils.LessonTimesStr
                var lessonPlace = dailySchedule[0]
                time = lessonPlace.time.timeString
                spansAppend(
                    res,
                    (lessonPlace.time.order + 1).toString() + ") " + time.start + "-" + time.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                for (lesson in lessonPlace.lessons) {
                    spansAppend(
                        res,
                        "\n" + lesson.type + getGroupText(lesson),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                        ForegroundColorSpan(
                            if (lesson.isImportant)
                                lessonTypeColors[0]
                            else
                                lessonTypeColors[1]
                        ),
                        RelativeSizeSpan(0.9f)
                    )
                    title = cutTitle(lesson.title)
                    res.append("\n" + title)
                }

                for (i in 1 until dailySchedule.size) {
                    lessonPlace = dailySchedule[i]
                    time = lessonPlace.time.timeString
                    spansAppend(
                        res,
                        "\n" + (lessonPlace.time.order + 1) + ") " + time.start + "-" + time.end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    for (lesson in lessonPlace.lessons) {
                        spansAppend(
                            res,
                            "\n" + lesson.type + getGroupText(lesson),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                            ForegroundColorSpan(
                                if (lesson.isImportant)
                                    lessonTypeColors[0]
                                else
                                    lessonTypeColors[1]
                            ),
                            RelativeSizeSpan(0.9f)
                        )
                        title = cutTitle(lesson.title)
                        res.append("\n" + title)
                    }
                }
            }
            viewBinding.textScheduleGrid.setText(res, TextView.BufferType.NORMAL)
        }

        private fun spansAppend(builder: SpannableStringBuilder, text: String, flags: Int, vararg spans: Any) {
            val start = builder.length
            builder.append(text)
            val length = builder.length
            for (span in spans) {
                builder.setSpan(span, start, length, flags)
            }
        }

        private fun getGroupText(lesson: Lesson): String {
            return if (lessonFeaturesSettings.showGroups && lessonFeaturesSettings.showTeachers)
                lesson.groups.joinToString(prefix = " ") { it.title }
            else
                ""
        }
    }
}