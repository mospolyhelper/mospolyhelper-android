package com.mospolytech.mospolyhelper.features.ui.schedule.calendar

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonPlace
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Event1
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// TODO: Return filter
class CalendarSevenAdapter(
    val schedule: Schedule?
) : RecyclerView.Adapter<CalendarSevenAdapter.ViewHolder>() {
    companion object {
        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
        private const val MAX_COUNT = 400
    }
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d.MM")
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
            .inflate(R.layout.item_schedule_calendar_seven, parent, false)
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
        private val lessonTime: TextView = view.findViewById(R.id.text_schedule_time_grid)
        private val lessonType: TextView = view.findViewById(R.id.text_schedule_grid)
        private val lessonPlace: LinearLayout = view.findViewById(R.id.linear_layout_schedule_grid)

        init {
            lessonPlace.setOnClickListener {
                (dayClick as Action1).invoke(firstPosDate.plusDays(adapterPosition.toLong()))
            }
        }

        fun bind() {
            if (schedule == null) return

            val date = firstPosDate.plusDays(adapterPosition.toLong())
            val dailySchedule = schedule.getLessons(date)
            setLessons(dailySchedule, date)
            setHead(date)
        }

        private fun setHead(date: LocalDate) {
            lessonTime.setTextColor(colorTitle)
            val today = LocalDate.now()

            if (date.dayOfYear == today.dayOfYear && date.year == today.year) {
                lessonTime.setTextColor(colorCurrentTitle)
            }
            lessonTime.setText(date.format(dateFormatter), TextView.BufferType.NORMAL)
        }

        private fun setLessons(dailySchedule: List<LessonPlace>, date: LocalDate) {
//            val res = SpannableStringBuilder()
//
//            if (dailySchedule.isNotEmpty()) {
//                var title: String
//                spansAppend(
//                    res,
//                    (dailySchedule[0].order + 1).toString() + ") ",
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//                    //QuoteSpan(colorParagraph),
//                    //BackgroundColorSpan(colorTimeBackground),
//                    TypefaceSpan("sans-serif-medium")
//                )
//
//                spansAppend(
//                    res,
//                    getShortType(dailySchedule[0].type),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//                    ForegroundColorSpan(
//                        if (dailySchedule[0].isImportant)
//                            lessonTypeColors[0]
//                        else
//                            lessonTypeColors[1]
//                    ),
//                    RelativeSizeSpan(0.9f),
//                    TypefaceSpan("sans-serif-medium"));
//
//                title = abbreviationFrom(dailySchedule[0].title)
//                res.append("\n" + title)
//
//
//                for (i in 1 until dailySchedule.size) {
//                    if (!dailySchedule[i].equalsTime(dailySchedule[i - 1])) {
//                        spansAppend(
//                            res, "\n" + (dailySchedule[i].order + 1) + ") ",
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//                            //QuoteSpan(colorParagraph),
//                            //BackgroundColorSpan(colorTimeBackground),
//                            TypefaceSpan("sans-serif-medium")
//                        )
//                    }
//                    spansAppend(
//                        res,
//                        getShortType(dailySchedule[i].type),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//                        ForegroundColorSpan(
//                            if (dailySchedule[i].isImportant)
//                                lessonTypeColors[0]
//                            else
//                                lessonTypeColors[1]
//                        ),
//                        RelativeSizeSpan(0.9f),
//                        TypefaceSpan("sans-serif-medium")
//                    )
//                    title = abbreviationFrom(dailySchedule[i].title)
//                    res.append("\n" + title)
//                }
//            }
//            lessonType.setText(res, TextView.BufferType.NORMAL);
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