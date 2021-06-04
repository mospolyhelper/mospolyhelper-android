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
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonPlace
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import com.mospolytech.mospolyhelper.domain.schedule.utils.cutTitle
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Event1
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// TODO: Return filter
class CalendarThreeAdapter(
    val schedule: Schedule?
) : RecyclerView.Adapter<CalendarThreeAdapter.ViewHolder>() {
    companion object {
        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
        private const val MAX_COUNT = 400
    }
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d MMMM")
    var firstPosDate: LocalDate = LocalDate.now()
    private var itemCount = 0

    val dayClick: Event1<LocalDate> = Action1()

    init {
        setCount()
        setFirstPosDate()
    }

    override fun getItemCount() = itemCount

    private fun setCount() {
        if (schedule == null) {
            itemCount = 0
        } else {
            itemCount = schedule.dateFrom.until(schedule.dateTo, ChronoUnit.DAYS).toInt() + 1
            if (itemCount !in 1..MAX_COUNT) {
                itemCount = MAX_COUNT
            }
        }
    }

    private fun setFirstPosDate() {
        firstPosDate = if (itemCount == MAX_COUNT) {
            LocalDate.now().minusDays((MAX_COUNT / 2).toLong())
        }
        else {
            schedule?.dateFrom ?: LocalDate.MIN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_schedule_calendar_three, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorParagraph: Int = view.context.getColor(R.color.calendarParagraph)
        val colorTimeBackground: Int = view.context.getColor(R.color.calendarTimeBackground)
        val colorTitle: Int = view.context.getColor(R.color.calendarTitle)
        val colorCurrentTitle: Int = view.context.getColor(R.color.calendarCurrentTitle)

        private val viewBinding by viewBinding(ItemScheduleCalendarThreeBinding::bind)


        init {
            viewBinding.linearLayoutScheduleGrid.setOnClickListener {
                (dayClick as Action1).invoke(firstPosDate.plusDays(adapterPosition.toLong()))
            }
        }

        fun bind() {
            if (schedule == null) return

            val date = firstPosDate.plusDays(bindingAdapterPosition.toLong())
            val dailySchedule = schedule.getLessons(date)
            setLessons(dailySchedule, date)
            setHead(date)
            setPadding(date)
        }

        private fun setPadding(date: LocalDate) {
            val dp4 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                itemView.resources.displayMetrics
            ).toInt()

            var paddingStart = dp4
            var paddingTop = dp4
            var paddingEnd = dp4
            var paddingBottom = dp4

            when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> {
                    //if (adapterPosition % 3 == 0) paddingStart = 0
                    paddingStart = 0
                    paddingEnd = 0
                    paddingBottom = 0
                }
                DayOfWeek.TUESDAY -> {
                    paddingStart = 0
                    paddingEnd = 0
                    paddingBottom = 0
                }
                DayOfWeek.WEDNESDAY -> {
                    paddingStart = 0
                    paddingEnd = 0
                    paddingBottom = 0
                }
                DayOfWeek.THURSDAY -> {
                    paddingStart = 0
                    paddingTop = 0
                    paddingEnd = 0
                    paddingBottom = 0
                }
                DayOfWeek.FRIDAY -> {
                    paddingStart = 0
                    paddingTop = 0
                    paddingEnd = 0
                }
                DayOfWeek.SATURDAY -> {
                    paddingStart = 0
                    paddingTop = 0
                    paddingEnd = 0
                }
                DayOfWeek.SUNDAY -> {
                    paddingStart = 0
                    if (bindingAdapterPosition % 3 == 2) paddingEnd = 0
                    paddingTop = 0
                }
                null -> {
                }
            }
            viewBinding.linearLayoutScheduleGrid.setPaddingRelative(paddingStart, 0, paddingEnd, paddingBottom)
        }

        private fun setHead(date: LocalDate) {
            viewBinding.textScheduleTimeGrid.setTextColor(colorTitle)
            val today = LocalDate.now()

            if (date.dayOfYear == today.dayOfYear && date.year == today.year) {
                viewBinding.textScheduleTimeGrid.setTextColor(colorCurrentTitle)
            }
            viewBinding.textScheduleTimeGrid.setText(date.format(dateFormatter), TextView.BufferType.NORMAL)
        }

        private fun setLessons(dailySchedule: List<LessonPlace>, date: LocalDate) {
            val res = SpannableStringBuilder()

            if (dailySchedule.isNotEmpty()) {
                var title: String
                var time: LessonTimeUtils.LessonTimesStr
                var lessonPlace = dailySchedule[0]
                time = lessonPlace.time.timeString
                spansAppend(
                    res,
                    (lessonPlace.time.order + 1).toString() + ") " + time.start + "-" + time.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    TypefaceSpan("sans-serif-medium")
                )
                for (lesson in lessonPlace.lessons) {
                    spansAppend(
                        res,
                        "\n" + lesson.type,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                        ForegroundColorSpan(
                            if (lesson.isImportant)
                                lessonTypeColors[0]
                            else
                                lessonTypeColors[1]
                        ),
                        RelativeSizeSpan(0.9f),
                        TypefaceSpan("sans-serif-medium")
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
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                        TypefaceSpan("sans-serif-medium")
                    )
                    for (lesson in lessonPlace.lessons) {
                        spansAppend(
                            res,
                            "\n" + lesson.type,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                            ForegroundColorSpan(
                                if (lesson.isImportant)
                                    lessonTypeColors[0]
                                else
                                    lessonTypeColors[1]
                            ),
                            RelativeSizeSpan(0.9f),
                            TypefaceSpan("sans-serif-medium")
                        )
                        title = cutTitle(lesson.title)
                        res.append("\n" + title)
                    }
                }
            }
            viewBinding.textScheduleGrid.setText(res, TextView.BufferType.NORMAL);
        }

        private fun spansAppend(builder: SpannableStringBuilder, text: String, flags: Int, vararg spans: Any) {
            val start = builder.length
            builder.append(text)
            val length = builder.length
            for (span in spans) {
                builder.setSpan(span, start, length, flags)
            }
        }
    }
}