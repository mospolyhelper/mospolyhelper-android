package com.mospolytech.mospolyhelper.ui.schedule

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.model.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.model.schedule.Schedule
import java.util.*

class LessonAdapter(
    val nullMessage: TextView,
    var dailySchedule: List<Lesson>,
    var filter: Schedule.Filter,
    var date: Calendar,
    var showEmptyLessons: Boolean,
    var showGroup: Boolean,
    var nightMode: Boolean,
    var disabledColor: Int,
    var headColor: Int,
    var headCurrentColor: Int
) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {
    companion object {
        val lessonTypeColors = listOf(
            0xeb4141,   // Exam, Credit,..
            0x29b6f6    // Other
        )
    }
    var orderMap: IntArray = IntArray(7)
    private var itemCount = 0
    var currLessonOrder = 0

    private val lessonClick = mutableSetOf<(Lesson) -> Unit>()

    init {
        orderMap.fill(-1)
        nullMessage.visibility = if (dailySchedule.isNotEmpty()) View.GONE else View.VISIBLE
        setCount()
    }

    override fun getItemCount() = itemCount

    private fun setCount() {
        if (dailySchedule.isEmpty()) {
            itemCount = 0;
            return
        }
        var localCurrLessonOrder = -1
        var fixedOrder = -1
        if (date == Calendar.getInstance()) {
            localCurrLessonOrder = Lesson.getOrder(Calendar.getInstance(), dailySchedule[0].group.isEvening)
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
        this.itemCount = this.dailySchedule.size
        if (showEmptyLessons)
        {
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

    fun buildSchedule(dailySchedule: List<Lesson>, scheduleFilter: Schedule.Filter, date: Calendar,
                      showEmptyLessons: Boolean, showGroup: Boolean)
    {
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

    fun addOnLessonClick(block: (Lesson) -> Unit) = lessonClick.add(block)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        view.isEnabled = false
        return ViewHolder(view) { lesson -> lessonClick.forEach { it(lesson) } }
    }

    fun ViewHolder.setHead(prevEqual: Boolean) {
        if (prevEqual) {
            val scale = lessonLayout.context.resources.displayMetrics.density;
            val dp18InPx = (18 * scale + 0.5f).toInt()
            val dp1InPx = (scale + 0.5f).toInt()
            lessonOrder.visibility = View.GONE
            lessonTime.visibility = View.GONE
            divider.visibility = View.VISIBLE
            indicator.visibility = View.GONE
            var par = divider.layoutParams as RelativeLayout.LayoutParams
            par.rightMargin = dp18InPx
            par.leftMargin = dp18InPx
            par.height = dp1InPx
        } else {
            if (currLessonOrder == lesson.order) {
                indicator.visibility = View.VISIBLE
                lessonTime.setTextColor(headCurrentColor)
            } else {
                indicator.visibility = View.GONE
                lessonTime.setTextColor(headColor)
            }

            val scale = lessonLayout.context.resources.displayMetrics.density
            val dp18InPx = (18 * scale + 0.5f).toInt()
            val dp2_5InPx = (2.5 * scale + 0.5f).toInt()

            lessonOrder.visibility = View.VISIBLE
            lessonTime.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
            var par = divider.layoutParams as RelativeLayout.LayoutParams
            par.rightMargin = 0
            par.leftMargin = 0
            par.height = dp2_5InPx
            val order = "#${lesson.order + 1}"
            lessonOrder.setText(order, TextView.BufferType.NORMAL)

            val (timeStart, timeEnd) = lesson.time
            val time = "$timeStart - $timeEnd"
            lessonTime.setText(time, TextView.BufferType.NORMAL)
        }

    }

    fun ViewHolder.setLessonType(enabled: Boolean) {
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

    fun ViewHolder.setAuditoriums(enabled: Boolean) {
        val auditoriums = SpannableStringBuilder()
        lessonAuditoriums.isEnabled = enabled
        if (lesson.auditoriums.isEmpty()) {
            lessonAuditoriums.setText("", TextView.BufferType.NORMAL)
            return
        }
        if (enabled) {
            for (i in lesson.auditoriums.indices) {
                val audTitle = HtmlCompat.fromHtml(lesson.auditoriums[i].title.toLowerCase(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
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
            if (lesson.auditoriums.isNotEmpty()) {
                var audTitle = HtmlCompat.fromHtml(lesson.auditoriums[lesson.auditoriums.lastIndex].title.toLowerCase(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
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
        } else {
            for (i in 0 until lesson.auditoriums.size - 1) {
                var audTitle = HtmlCompat.fromHtml(lesson.auditoriums[i].title.toLowerCase(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
                auditoriums
                    .append(audTitle,
                        ForegroundColorSpan(disabledColor),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(", ",
                        ForegroundColorSpan(disabledColor),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (lesson.auditoriums.isNotEmpty()) {
                var audTitle = HtmlCompat.fromHtml(lesson.auditoriums[lesson.auditoriums.lastIndex].title.toLowerCase(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY)
                auditoriums.append(audTitle,
                    ForegroundColorSpan(disabledColor),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        lessonAuditoriums.setText(auditoriums, TextView.BufferType.NORMAL);
        auditoriums.clear()
    }

    fun ViewHolder.setTitle(enabled: Boolean) {
        val title = lesson.title
        lessonTitle.setText(title, TextView.BufferType.NORMAL)
        lessonTitle.isEnabled = enabled
    }

    fun ViewHolder.setTeachers(enabled: Boolean) {
        val teachers = lesson.teachers.joinToString(", ") { it.getShortName() }
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
            var flag = false
            var lastNotEmpty = -1
            for (i in 1 until 6) {
                if (this.orderMap[i] == -1) {
                    fixedPos--
                    if (lastNotEmpty == fixedPos) {
                        viewHolder.lesson = Lesson.getEmpty(i)
                        flag = true
                        break
                    }
                } else {
                    lastNotEmpty = this.orderMap[i]
                    if (fixedPos <= this.orderMap[i]) {
                        break
                    }
                }
            }
            if (flag) {
                viewHolder.lesson = this.dailySchedule[fixedPos];
            }
        } else {
            viewHolder.lesson = dailySchedule[position]
        }

        val scale = viewHolder.lessonLayout.context.resources.displayMetrics.density;
        val dp8InPx = (9 * scale + 0.5f).toInt()
        val dp18InPx = (18 * scale + 0.5f).toInt()
        if (viewHolder.lesson.isEmpty) {
            viewHolder.setHead(false)
            viewHolder.setLessonType(true)
            viewHolder.setAuditoriums(true)
            viewHolder.setTitle(true)
            viewHolder.setTeachers(true)
            viewHolder.lessonLayout.background =
                viewHolder.lessonLayout.context.getDrawable(R.drawable.shape_lesson)
            (viewHolder.lessonLayout.layoutParams as FrameLayout.LayoutParams)
                .setMargins(dp18InPx, if (position == 0) dp8InPx else 0, dp18InPx, dp8InPx)
            return
        }

        viewHolder.setHead(fixedPos != 0 && this.dailySchedule[fixedPos - 1].equalsTime(viewHolder.lesson));

        val enabledFrom = viewHolder.lesson.dateFrom <= date || filter.dateFilter != Schedule.Filter.DateFilter.Desaturate
        val enabledTo = viewHolder.lesson.dateTo >= date || filter.dateFilter != Schedule.Filter.DateFilter.Desaturate
        var enabled = if (viewHolder.lesson.isImportant) enabledTo else enabledFrom && enabledTo

        if (fixedPos != dailySchedule.lastIndex && dailySchedule[fixedPos + 1].equalsTime(viewHolder.lesson)) {
            (viewHolder.lessonLayout.layoutParams as FrameLayout.LayoutParams)
                .setMargins(dp18InPx, if (position == 0) dp8InPx else 0, dp18InPx, 0);
            if (fixedPos != 0 && dailySchedule[fixedPos - 1].equalsTime(viewHolder.lesson)) {
                viewHolder.lessonLayout.background =
                    viewHolder.lessonLayout.context.getDrawable(R.drawable.shape_lesson_middle);
            } else {
                viewHolder.lessonLayout.background =
                    viewHolder.lessonLayout.context.getDrawable(R.drawable.shape_lesson_top);
            }
        } else {
            (viewHolder.lessonLayout.layoutParams as FrameLayout.LayoutParams)
                .setMargins(dp18InPx, if (position == 0) dp8InPx else 0, dp18InPx, dp8InPx);
            if (fixedPos != 0 && this.dailySchedule[fixedPos - 1].equalsTime(viewHolder.lesson)) {
                viewHolder.lessonLayout.background =
                    viewHolder.lessonLayout.context.getDrawable(R.drawable.shape_lesson_bottom);
            } else {
                viewHolder.lessonLayout.background =
                    viewHolder.lessonLayout.context.getDrawable(R.drawable.shape_lesson);
            }
        }

        viewHolder.setLessonType(enabled)
        viewHolder.setAuditoriums(enabled)
        viewHolder.setTitle(enabled)
        viewHolder.setTeachers(enabled)
    }

    class ViewHolder(val view: View, onLessonClick: (Lesson) -> Unit) : RecyclerView.ViewHolder(view) {
        var lessonTitle = view.findViewById<TextView>(R.id.text_schedule_title)!!
        var lessonTime = view.findViewById<TextView>(R.id.text_schedule_time)!!
        var lessonOrder = view.findViewById<TextView>(R.id.text_schedule_order)!!
        var lessonType = view.findViewById<TextView>(R.id.text_schedule_type)!!
        var lessonTeachers = view.findViewById<TextView>(R.id.text_schedule_teachers)!!
        var lessonAuditoriums = view.findViewById<TextView>(R.id.text_schedule_auditoriums)!!
        var lessonLayout = view.findViewById<RelativeLayout>(R.id.layout_schedule)!!
        var favoriteIcon = view.findViewById<View>(R.id.schedule_favorite_icon)!!
        var noteIcon = view.findViewById<View>(R.id.schedule_note_icon)!!
        var divider = view.findViewById<View>(R.id.schedule_divider)!!
        var indicator = view.findViewById<View>(R.id.indicator)!!
        var lesson: Lesson = Lesson.getEmpty(0)

        init {
            lessonLayout.setOnClickListener { onLessonClick(lesson) }
        }
    }
}