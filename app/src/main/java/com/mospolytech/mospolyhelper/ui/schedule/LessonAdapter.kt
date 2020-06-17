package com.mospolytech.mospolyhelper.ui.schedule

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.models.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Event1
import java.time.LocalDate
import java.time.LocalTime

class LessonAdapter(
    val nullMessage: TextView,
    var dailySchedule: List<Lesson>,
    var filter: Schedule.Filter,
    var date: LocalDate,
    var showEmptyLessons: Boolean,
    var showGroup: Boolean,
    var nightMode: Boolean,
    var disabledColor: Int,
    var headColor: Int,
    var headCurrentColor: Int
) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {
    companion object {
        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
    }
    var orderMap: IntArray = IntArray(7)
    private var itemCount = 0
    var currLessonOrder = 0

    val lessonClick: Event1<Lesson> = Action1()

    init {
        orderMap.fill(-1)
        nullMessage.visibility = if (dailySchedule.isNotEmpty()) View.GONE else View.VISIBLE
        setCount()
    }

    override fun getItemCount() = itemCount

    private fun setCount() {
        if (dailySchedule.isEmpty()) {
            itemCount = 0
            return
        }
        var localCurrLessonOrder = -1
        var fixedOrder = -1
        val today = LocalDate.now()
        if (date.dayOfYear == today.dayOfYear && date.year == today.year) {
            localCurrLessonOrder = Lesson.getOrder(LocalTime.now(), dailySchedule[0].group.isEvening)
            for (lesson in dailySchedule)
            {
                if (filter.dateFilter != Schedule.Filter.DateFilter.Desaturate ||
                    (date >= lesson.dateFrom && date <= lesson.dateTo)) {
                    fixedOrder = lesson.order
                }
                if (fixedOrder >= localCurrLessonOrder) {
                    break
                }
            }
        }
        this.currLessonOrder = fixedOrder
        this.itemCount = dailySchedule.size
        if (showEmptyLessons) {
            if (fixedOrder >= localCurrLessonOrder) {
                this.currLessonOrder = localCurrLessonOrder
            }
            for (i in dailySchedule.indices) {
                this.orderMap[this.dailySchedule[i].order] = i
            }
            val maxOrder = dailySchedule[dailySchedule.size - 1].order
            for (i in 0 until maxOrder) {
                if (this.orderMap[i] == -1) {
                    this.itemCount++
                }
            }
        }
    }

    fun buildSchedule(
        dailySchedule: List<Lesson>,
        scheduleFilter: Schedule.Filter,
        date: LocalDate,
        showEmptyLessons: Boolean,
        showGroup: Boolean
    ) {
        this.showEmptyLessons = showEmptyLessons
        this.showGroup = showGroup
        orderMap.fill(-1)
        this.dailySchedule = dailySchedule
        this.date = date
        this.filter = scheduleFilter
        nullMessage.visibility = if (this.dailySchedule.isNotEmpty()) View.GONE else View.VISIBLE
        setCount()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        view.isEnabled = false
        return ViewHolder(view) { (lessonClick as Action1).invoke(it) }
    }

    private fun ViewHolder.setHead(prevEqual: Boolean) {
        if (prevEqual) {
            lessonTime.visibility = View.INVISIBLE
        } else {
            if (currLessonOrder == lesson.order) {
                lessonTime.setTextColor(headCurrentColor)
            } else {
                lessonTime.setTextColor(headColor)
            }
            lessonTime.visibility = View.VISIBLE
        }
        val (timeStart, timeEnd) = lesson.time
        val time = "$timeStart"
        lessonTime.setText(time, TextView.BufferType.NORMAL)
    }

    private fun ViewHolder.setFooter(nextEqual: Boolean, isLast: Boolean) {
        val (_, timeEnd) = lesson.time
        if (nextEqual) {
            lessonFooter.visibility = View.GONE
        } else {
            lessonFooter.visibility = View.VISIBLE
            when {
                lesson.isEmpty -> {
                    if (orderMap[lesson.order + 1] != -1) {
                        val (nextTimeStart, _) = Lesson.getTime(lesson.order + 1, lesson.group.isEvening)
                        lessonFooter.text = "нет занятий до $nextTimeStart"
                    } else {
                        lessonFooter.text = ""
                    }
                }
                isLast -> {
                    lessonFooter.text = "конец занятий в $timeEnd"
                }
                lesson.order == 2 -> {
                    lessonFooter.text = "в $timeEnd перерыв на 40 минут"
                }
                else -> {
                    lessonFooter.text = "в $timeEnd перерыв на 10 минут"
                }
            }
        }
    }

    private fun ViewHolder.setLessonType(enabled: Boolean) {
        val type = if (showGroup)
            lesson.type.toUpperCase() + "  " + lesson.group.title
        else
            lesson.type.toUpperCase()

        lessonType.setTextColor(
            if (enabled)
                (if (lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
            else
                disabledColor
        )
        lessonType.setText(type, TextView.BufferType.NORMAL);
        lessonType.isEnabled = enabled;
    }

    private fun ViewHolder.setAuditoriums(enabled: Boolean) {
        val auditoriums = SpannableStringBuilder()
        lessonAuditoriums.isEnabled = enabled
        if (lesson.auditoriums.isEmpty()) {
            lessonAuditoriums.visibility = View.GONE
            lessonAuditoriums.setText("", TextView.BufferType.NORMAL)
            return
        }
        lessonAuditoriums.visibility = View.VISIBLE
        if (enabled) {
            for (i in 0 until lesson.auditoriums.size - 1) {
                val auditorium = lesson.auditoriums[i]
                val audTitle = HtmlCompat.fromHtml(auditorium.title.toLowerCase(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
                val color = convertColorFromString(auditorium.color)
                if (color != null) {
                    auditoriums
                        .append(audTitle, ForegroundColorSpan(color),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        .append(", ", ForegroundColorSpan(color),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    auditoriums
                        .append(audTitle)
                        .append(", ")
                }
            }
            val lastAuditorium = lesson.auditoriums.last()
            val audTitle = HtmlCompat.fromHtml(lastAuditorium.title.toLowerCase(),
                HtmlCompat.FROM_HTML_MODE_LEGACY)
            val color = convertColorFromString(lastAuditorium.color)
            if (color != null) {
                auditoriums.append(audTitle,
                    ForegroundColorSpan(color),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                auditoriums.append(audTitle)
            }
        } else {
            for (i in 0 until lesson.auditoriums.size - 1) {
                val audTitle = HtmlCompat.fromHtml(lesson.auditoriums[i].title.toLowerCase(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
                auditoriums
                    .append(audTitle,
                        ForegroundColorSpan(disabledColor),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(", ",
                        ForegroundColorSpan(disabledColor),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val audTitle = HtmlCompat.fromHtml(lesson.auditoriums.last().title.toLowerCase(),
                HtmlCompat.FROM_HTML_MODE_LEGACY)
            auditoriums.append(audTitle,
                ForegroundColorSpan(disabledColor),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        lessonAuditoriums.setText(auditoriums, TextView.BufferType.NORMAL)
        auditoriums.clear()
    }

    private fun convertColorFromString(colorString: String): Int? {
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

    private fun ViewHolder.setTitle(enabled: Boolean) {
        val title = lesson.title
        if (title.isEmpty()) {
            lessonTitle.visibility = View.GONE
        } else {
            lessonTitle.visibility = View.VISIBLE
        }
        lessonTitle.setText(title, TextView.BufferType.NORMAL)
        lessonTitle.isEnabled = enabled
    }

    private fun ViewHolder.setTeachers(enabled: Boolean) {
        val teachers = if (lesson.teachers.size == 1)
            lesson.teachers.first().getFullName()
        else
            lesson.teachers.joinToString(", ") { it.getShortName() }

        if (teachers.isEmpty()) {
            lessonTeachers.visibility = View.GONE
        } else {
            lessonTeachers.setText(teachers, TextView.BufferType.NORMAL)
            lessonTeachers.isEnabled = enabled
            lessonTeachers.visibility = View.VISIBLE
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var fixedPos = position
        if (showEmptyLessons) {
            var notEmpty = true
            var lastNotEmpty = -1
            for (i in 0 until 6) {
                if (orderMap[i] == -1) {
                    fixedPos--
                    if (lastNotEmpty == fixedPos) {
                        viewHolder.lesson = Lesson.getEmpty(i)
                        notEmpty = false
                        break
                    }
                } else {
                    lastNotEmpty = orderMap[i]
                    if (fixedPos <= orderMap[i]) {
                        break
                    }
                }
            }
            if (notEmpty) {
                viewHolder.lesson = dailySchedule[fixedPos]
            }
        } else {
            viewHolder.lesson = dailySchedule[position]
        }

        val scale = viewHolder.lessonLayout.context.resources.displayMetrics.density
        val dp9InPx = (9 * scale + 0.5f).toInt()
        val dp18InPx = (18 * scale + 0.5f).toInt()
        if (viewHolder.lesson.isEmpty) {
            viewHolder.setHead(false)
            viewHolder.setFooter(false, position == itemCount - 1)
            viewHolder.setLessonType(true)
            viewHolder.setAuditoriums(true)
            viewHolder.setTitle(true)
            viewHolder.setTeachers(true)
            if (position == 0) {
                (viewHolder.lessonLayout.layoutParams as RelativeLayout.LayoutParams).topMargin = dp9InPx
            } else {
                (viewHolder.lessonLayout.layoutParams as RelativeLayout.LayoutParams).topMargin = dp9InPx
            }
            return
        }

        val prevEqual = fixedPos != 0 && dailySchedule[fixedPos - 1].equalsTime(viewHolder.lesson)
        val nextEqual = (position != itemCount - 1) && dailySchedule[fixedPos + 1].equalsTime(viewHolder.lesson)

        viewHolder.setHead(prevEqual)
        viewHolder.setFooter(nextEqual, position == itemCount - 1)

        if (position == itemCount - 1) {
            val tv = TypedValue()
            if (viewHolder.lessonLayout.context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, viewHolder.lessonLayout.context.resources.displayMetrics)
                (viewHolder.lessonFooter.layoutParams as RelativeLayout.LayoutParams).bottomMargin = (actionBarHeight * 1.6).toInt()
            }
        } else {
            (viewHolder.lessonFooter.layoutParams as RelativeLayout.LayoutParams).bottomMargin = 0
        }

        if (position == 0) {
            (viewHolder.lessonLayout.layoutParams as RelativeLayout.LayoutParams).topMargin = dp9InPx * 1
            val params = (viewHolder.lessonTime.layoutParams as RelativeLayout.LayoutParams)
            params.topMargin = dp9InPx * 1
        } else {
            (viewHolder.lessonLayout.layoutParams as RelativeLayout.LayoutParams).topMargin = dp9InPx
            val params = (viewHolder.lessonTime.layoutParams as RelativeLayout.LayoutParams)
            params.topMargin = dp9InPx
        }
        val enabledFrom = viewHolder.lesson.dateFrom <= date || filter.dateFilter != Schedule.Filter.DateFilter.Desaturate
        val enabledTo = viewHolder.lesson.dateTo >= date || filter.dateFilter != Schedule.Filter.DateFilter.Desaturate
        val enabled = if (viewHolder.lesson.isImportant) enabledTo else enabledFrom && enabledTo


        viewHolder.setLessonType(enabled)
        viewHolder.setAuditoriums(enabled)
        viewHolder.setTitle(enabled)
        viewHolder.setTeachers(enabled)
    }

    class ViewHolder(val view: View, onLessonClick: (Lesson) -> Unit) : RecyclerView.ViewHolder(view) {
        val lessonTitle = view.findViewById<TextView>(R.id.text_schedule_title)!!
        val lessonTime = view.findViewById<TextView>(R.id.text_schedule_time)!!
        val lessonType = view.findViewById<TextView>(R.id.text_schedule_type)!!
        val lessonTeachers = view.findViewById<TextView>(R.id.text_schedule_teachers)!!
        val lessonAuditoriums = view.findViewById<TextView>(R.id.text_schedule_auditoriums)!!
        val lessonLayout = view.findViewById<LinearLayout>(R.id.layout_schedule)!!
        val lessonPlace = view.findViewById<RelativeLayout>(R.id.layout_lesson_place)!!
        val lessonFooter = view.findViewById<TextView>(R.id.text_schedule_end)
        var lesson: Lesson = Lesson.getEmpty(0)

        init {
            lessonPlace.setOnClickListener { onLessonClick(lesson) }
        }
    }
}