package com.mospolytech.mospolyhelper.ui.schedule.calendar

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.models.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CalendarAdapter(
    var schedule: Schedule,
    var scheduleFilter: Schedule.Filter,
    var showGroup: Boolean,
    val colorParagraph: Int,
    val colorTimeBackground: Int,
    val colorTitle: Int,
    val colorCurrentTitle: Int
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    companion object {
        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
    }
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, d MMMM")
    var firstPosDate: LocalDate = LocalDate.now()
    private var itemCount = 0

    private val dayClick = mutableSetOf<(LocalDate) -> Unit>()

    init {
        setCount()
        setFirstPosDate()
    }

    fun addOnDayClick(block: (LocalDate) -> Unit) = dayClick.add(block)

    override fun getItemCount() = itemCount

    fun setCount() {
        itemCount = schedule.dateFrom.until(schedule.dateTo, ChronoUnit.DAYS).toInt() + 1
        if (itemCount > 400 || itemCount < 0) {
            itemCount = 400
        }

    }

    fun setFirstPosDate() {
        firstPosDate = if (itemCount == 400)
            LocalDate.now().minusDays(200)
        else
            schedule.dateFrom
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_daily_schedule, parent, false)
        var vh = ViewHolder(view)
        vh.lessonPlace.setOnClickListener {
            dayClick.forEach {
                it(firstPosDate.plusDays(vh.layoutPosition.toLong()))
            }
        }

        return vh
    }

    fun ViewHolder.setHead(date: LocalDate) {
        val res = SpannableStringBuilder()
        lessonTime.setTextColor(colorTitle)
        val today = LocalDate.now()

        if (date.dayOfYear == today.dayOfYear && date.year == today.year) {
            lessonTime.setTextColor(colorCurrentTitle)
        }
        lessonTime.setText(date.format(dateFormatter), TextView.BufferType.NORMAL) // .replace('.', '\0') TODO Check it
    }

    fun ViewHolder.setLessons(dailySchedule: List<Lesson>, date: LocalDate) {
        val res = SpannableStringBuilder()

        if (dailySchedule.isNotEmpty()) {
            var title: String
            var time = dailySchedule[0].time
            spansAppend(
                res,
                (dailySchedule[0].order + 1).toString() + ") " + time.first + "-" + time.second,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                QuoteSpan(colorParagraph),
                BackgroundColorSpan(colorTimeBackground),
                TypefaceSpan("sans-serif-medium")
            )

            if (showGroup) {
                spansAppend(
                    res,
                    "\n" + dailySchedule[0].type.toUpperCase() + "  " + dailySchedule[0].group.title,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    ForegroundColorSpan(
                        if (dailySchedule[0].isImportant)
                            lessonTypeColors[0]
                        else
                            lessonTypeColors[1]
                    ),
                    RelativeSizeSpan(0.8f),
                    TypefaceSpan("sans-serif-medium"))
            } else {
                spansAppend(
                    res,
                    "\n" + dailySchedule[0].type.toUpperCase(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    ForegroundColorSpan(
                        if (dailySchedule[0].isImportant)
                            lessonTypeColors[0]
                        else
                            lessonTypeColors[1]
                    ),
                    RelativeSizeSpan(0.8f),
                    TypefaceSpan("sans-serif-medium"));
            }

            title = dailySchedule[0].title;
            res.append("\n" + title);


            for (i in 1 until dailySchedule.size) {
                if (!dailySchedule[i].equalsTime(dailySchedule[i - 1])) {
                    time = dailySchedule[i].time
                    spansAppend(
                        res, "\n" + (dailySchedule[i].order + 1) + ") " + time.first + "-" + time.second,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                        QuoteSpan(colorParagraph),
                        BackgroundColorSpan(colorTimeBackground),
                        TypefaceSpan("sans-serif-medium")
                    )
                }
                if (showGroup) {
                    spansAppend(
                        res,
                        "\n" + dailySchedule[i].type.toUpperCase() + "  " + dailySchedule[i].group.title,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                        ForegroundColorSpan(
                            if (dailySchedule[i].isImportant)
                                lessonTypeColors[0]
                            else
                                lessonTypeColors[1]
                        ),
                        RelativeSizeSpan(0.8f),
                        TypefaceSpan("sans-serif-medium")
                    )
                } else {
                    spansAppend(
                        res,
                        "\n" + dailySchedule[i].type.toUpperCase(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                        ForegroundColorSpan(
                            if (dailySchedule[i].isImportant)
                                lessonTypeColors[0]
                            else
                                lessonTypeColors[1]
                        ),
                    RelativeSizeSpan(0.8f),
                    TypefaceSpan("sans-serif-medium")
                    )
                }
                title = dailySchedule[i].title;
                res.append("\n" + title);
            }
        }
        lessonType.setText(res, TextView.BufferType.NORMAL);
    }

    fun spansAppend(builder: SpannableStringBuilder, text: String, flags: Int, vararg spans: Any) {
        val start = builder.length
        builder.append(text)
        val length = builder.length
        for (span in spans) {
            builder.setSpan(span, start, length, flags)
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val date: LocalDate = firstPosDate.plusDays(position.toLong())
        val dailySchedule = this.schedule.getSchedule(date, scheduleFilter);
        viewHolder.setLessons(dailySchedule, date)
        viewHolder.setHead(date);
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lessonTime: TextView = view.findViewById(R.id.text_schedule_time_grid)
        val lessonType: TextView = view.findViewById(R.id.text_schedule_grid)
        val lessonPlace: LinearLayout = view.findViewById(R.id.linear_layout_schedule_grid)
    }
}