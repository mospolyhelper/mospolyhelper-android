package com.mospolytech.mospolyhelper.ui.schedule

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Event1
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class LessonAdapter(
    var dailySchedule: List<Lesson>,
    var filter: Schedule.Filter,
    var date: LocalDate,
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

    private var itemCount = 0
    var currLessonOrder = 0

    val lessonClick: Event1<Lesson> = Action1()

    init {
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
            localCurrLessonOrder =
                Lesson.getOrder(LocalTime.now(), dailySchedule[0].group.isEvening)
            for (lesson in dailySchedule) {
                if (filter.dateFilter != Schedule.Filter.DateFilter.Desaturate ||
                    (date in (lesson.dateFrom..lesson.dateTo))
                ) {
                    fixedOrder = lesson.order
                }
                if (fixedOrder >= localCurrLessonOrder) {
                    break
                }
            }
        }
        this.currLessonOrder = fixedOrder
        this.itemCount = dailySchedule.size
        if (fixedOrder >= localCurrLessonOrder) {
            this.currLessonOrder = localCurrLessonOrder
        }
    }

    fun update(
        dailySchedule: List<Lesson>,
        scheduleFilter: Schedule.Filter,
        date: LocalDate,
        showGroup: Boolean
    ) {
        this.showGroup = showGroup
        this.dailySchedule = dailySchedule
        this.date = date
        this.filter = scheduleFilter
        setCount()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view) { (lessonClick as Action1).invoke(it) }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(this)
    }

    class ViewHolder(val view: View, onLessonClick: (Lesson) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private lateinit var adapter: LessonAdapter
        private val lessonTitle = view.findViewById<TextView>(R.id.text_schedule_title)!!
        private val lessonTime = view.findViewById<TextView>(R.id.text_schedule_time)!!
        private val lessonTime2 = view.findViewById<TextView>(R.id.text_schedule_time2)!!
        private val lessonType = view.findViewById<TextView>(R.id.text_schedule_type)!!
        private val lessonTeachers = view.findViewById<TextView>(R.id.text_schedule_teachers)!!
        private val lessonAuditoriums = view.findViewById<TextView>(R.id.text_schedule_auditoriums)!!
        private val lessonPlace = view.findViewById<LinearLayout>(R.id.layout_lesson)!!
        private val timeline = view.findViewById<View>(R.id.lesson_timeline)
        private val lessonInfo = view.findViewById<TextView>(R.id.lesson_info)
        private val lessonTimeLayout = view.findViewById<FrameLayout>(R.id.layout_lesson_time)
        private var lesson: Lesson = Lesson.getEmpty(0)

        init {
            lessonPlace.setOnClickListener { onLessonClick(lesson) }
            lessonTimeLayout.minimumHeight = (
                    lessonTime.marginTop * 1.7f + lessonTime2.marginBottom * 1.7f +
                            lessonTime.paint.fontSpacing + lessonTime2.paint.fontSpacing
                    ).toInt()
            val dpTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, view.resources.displayMetrics)
            val dpBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.7f, view.resources.displayMetrics)
            val timeLineParams = (timeline.layoutParams as FrameLayout.LayoutParams)
            timeLineParams.topMargin = (lessonTime.marginTop + lessonTime.paint.fontSpacing + dpTop).toInt()
            timeLineParams.bottomMargin = (lessonTime2.marginBottom + lessonTime2.paint.fontSpacing + dpBottom).toInt()
        }

        // ViewHolder class is not inner because recycler views have common pool
        fun bind(adapter: LessonAdapter) {
            this.adapter = adapter
            lesson = adapter.dailySchedule[layoutPosition]


            val lessonIsEmpty = lesson.isEmpty

            val prevEqual =
                !lessonIsEmpty && (layoutPosition != 0 && adapter.dailySchedule[layoutPosition - 1].equalsTime(lesson))
            val nextEqual =
                !lessonIsEmpty && (layoutPosition != adapter.itemCount - 1) && adapter.dailySchedule[layoutPosition + 1].equalsTime(
                    lesson
                )

            setTime(prevEqual, nextEqual, layoutPosition)

            if (layoutPosition == adapter.itemCount - 1) {
                val tv = TypedValue()
                if (lessonPlace.context.theme.resolveAttribute(
                        android.R.attr.actionBarSize,
                        tv,
                        true
                    )
                ) {
                    val actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data,
                        lessonPlace.context.resources.displayMetrics
                    )
                    view.setPadding(0, 0,0, (actionBarHeight * 1.6).toInt())

                }
            } else {
                view.setPadding(0, 0,0, 0)
            }
            val enabledFrom =
                lesson.dateFrom <= adapter.date || adapter.filter.dateFilter != Schedule.Filter.DateFilter.Desaturate
            val enabledTo =
                lesson.dateTo >= adapter.date || adapter.filter.dateFilter != Schedule.Filter.DateFilter.Desaturate
            val enabled =
                if (lesson.isImportant) lessonIsEmpty || enabledTo else lessonIsEmpty || enabledFrom && enabledTo

            setFooter(nextEqual, enabled)
            setInfo(nextEqual)
            setLessonType(enabled)
            setAuditoriums(enabled)
            setTitle(enabled)
            setTeachers(enabled)
        }

        private fun setTime(prevEqual: Boolean, nextEqual: Boolean, position: Int) {
            //val set = ConstraintSet()
            val dp5 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, lessonPlace.context.resources.displayMetrics).toInt()
            //set.clone(lessonPlace)
            if (prevEqual && nextEqual) {
//                set.setMargin(lessonTimeLayout.id, ConstraintSet.TOP, 0)
//                set.setMargin(lessonTimeLayout.id, ConstraintSet.BOTTOM, 0)
//                set.connect(timeline.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
//                set.connect(timeline.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                lessonTimeLayout.background = ColorDrawable(lessonPlace.context.getColor(R.color.lessonTimeBackground))
            } else {
                if (prevEqual) {
//                    set.setMargin(lessonTimeLayout.id, ConstraintSet.TOP, 0)
//                    set.setMargin(lessonTimeLayout.id, ConstraintSet.BOTTOM, dp5)
                    lessonTimeLayout.background = lessonPlace.context.getDrawable(R.drawable.shape_lesson_time_bottom)
//                    set.connect(timeline.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
//                    set.connect(timeline.id, ConstraintSet.BOTTOM, lessonTime2.id, ConstraintSet.TOP)
                } else {
//                    set.setMargin(lessonTimeLayout.id, ConstraintSet.TOP, dp5)
                    if (nextEqual) {
//                        set.setMargin(lessonTimeLayout.id, ConstraintSet.TOP, dp5)
//                        set.setMargin(lessonTimeLayout.id, ConstraintSet.BOTTOM, 0)
                        lessonTimeLayout.background = lessonPlace.context.getDrawable(R.drawable.shape_lesson_time_upper)
//                        set.connect(timeline.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
//                        set.connect(timeline.id, ConstraintSet.TOP, lessonTime.id, ConstraintSet.BOTTOM)
                    } else {
//                        set.setMargin(lessonTimeLayout.id, ConstraintSet.BOTTOM, dp5)
//                        set.setMargin(lessonTimeLayout.id, ConstraintSet.TOP, dp5)
//                        set.connect(timeline.id, ConstraintSet.BOTTOM, lessonTime2.id, ConstraintSet.TOP)
//                        set.connect(timeline.id, ConstraintSet.TOP, lessonTime.id, ConstraintSet.BOTTOM)
                        lessonTimeLayout.background = lessonPlace.context.getDrawable(R.drawable.shape_lesson_time)
                    }
                }
            }
            if (lesson.isEmpty) {
                //lessonTimeLayout.visibility = View.GONE
            } else {
                //lessonTimeLayout.visibility = View.VISIBLE
            }
            if (prevEqual) {
                lessonTime.visibility = View.INVISIBLE
                //set.connect(lessonTimeLayout.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                //set.constrainHeight(lessonTimeLayout.id, ConstraintSet.MATCH_CONSTRAINT)
            } else {
                if (lesson.isEmpty) {
                    //lessonTime.background = lessonPlace.context.getDrawable(R.drawable.shape_lesson_time)
                    lessonTime.setTextColor(adapter.disabledColor)
                } else if (adapter.currLessonOrder == lesson.order) {
                    //lessonTime.background = lessonPlace.context.getDrawable(R.drawable.shape_lesson_time_upper)
                    lessonTime.setTextColor(adapter.headCurrentColor)
                } else {
                    //lessonTime.background = lessonPlace.context.getDrawable(R.drawable.shape_lesson_time_upper)
                    val deltaOrder = adapter.currLessonOrder - lesson.order
                    if (deltaOrder in 1..position && adapter.dailySchedule[position - deltaOrder].isEmpty) {
                        lessonTime.setTextColor(adapter.headCurrentColor)
                    } else {
                        lessonTime.setTextColor(adapter.headColor)
                    }
                }
                lessonTime.visibility = View.VISIBLE
            }
            if (nextEqual || lesson.isEmpty) {
                lessonTime2.visibility = View.GONE
            } else {
                if (adapter.currLessonOrder == lesson.order) {
                    lessonTime2.setTextColor(adapter.headCurrentColor)
                } else {
                    val deltaOrder = adapter.currLessonOrder - lesson.order
                    if (deltaOrder in 1..position && adapter.dailySchedule[position - deltaOrder].isEmpty) {
                        lessonTime2.setTextColor(adapter.headCurrentColor)
                    } else {
                        lessonTime2.setTextColor(adapter.headColor)
                    }
                }
                lessonTime2.visibility = View.VISIBLE
            }
            val (timeStart, timeEnd) = lesson.time
            lessonTime.setText(timeStart, TextView.BufferType.NORMAL)
            lessonTime2.setText(timeEnd, TextView.BufferType.NORMAL)
        }

        private fun setFooter(nextEqual: Boolean, enabled: Boolean) {
            if (lesson.isEmpty) {
                timeline.visibility = View.GONE
            } else {
                timeline.background = ColorDrawable(if (enabled)
                    (if (lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
                else
                    adapter.disabledColor
                )
                timeline.visibility = View.VISIBLE
            }
            lessonPlace.background = null
//            if (nextEqual) {
//                lessonPlace.background = null
//            } else {
//                lessonPlace.background = lessonPlace.context.getDrawable(R.drawable.bottom_stroke)
//            }
        }

        private fun setInfo(nextEqual: Boolean) {
            if (lesson.isEmpty || nextEqual) {
                lessonInfo.visibility = View.GONE
                return
            }
            var info: String
            if (layoutPosition + 1 < adapter.dailySchedule.size) {
                val nextLesson = adapter.dailySchedule[layoutPosition + 1]
                when {
                    lesson.order + 1 < nextLesson.order -> {
                        val totalMinutes = lesson.localTime.second.until(
                            nextLesson.localTime.first, ChronoUnit.MINUTES
                        )
                        val windowTimeHours = totalMinutes / 60L
                        val windowTimeMinutes = totalMinutes % 60
                        // *1 час .. *2, *3, *4 часа .. *5, *6, *7, *8, *9, *0 часов .. искл. - 11 - 14
                        val lastNumberOfHours = windowTimeHours % 10
                        val endingHours = when {
                            windowTimeHours in 11L..14L -> "ов"
                            lastNumberOfHours == 1L -> ""
                            lastNumberOfHours in 2L..4L -> "а"
                            else -> "ов"
                        }
                        info = "окно в $windowTimeHours час$endingHours"
                        if (windowTimeMinutes != 0L) {
                            // *1 минута .. *2, *3, *4 минуты .. *5, *6, *7, *8, *9, *0 минут .. искл. - 11 - 14
                            val lastNumberOfMinutes = windowTimeMinutes % 10
                            val endingMinutes = when {
                                windowTimeMinutes in 11L..14L -> ""
                                lastNumberOfMinutes == 1L -> "а"
                                lastNumberOfMinutes in 2L..4L -> "ы"
                                else -> ""
                            }
                            info += " $windowTimeMinutes минут$endingMinutes"
                        }
                        lessonInfo.text = info
                        val id = when  {
                            totalMinutes < 180 -> R.drawable.ic_baseline_fastfood_24
                            totalMinutes < 270 -> R.drawable.ic_round_sports_esports_24
                            else -> R.drawable.ic_round_sports_volleyball_24
                        }
                        lessonInfo.setCompoundDrawablesWithIntrinsicBounds(id,
                            0, 0, 0)
                        lessonInfo.visibility = View.VISIBLE
                    }
                    lesson.order == 2 -> {
                        info = "большой перерыв на 40 минут"
                        lessonInfo.text = info
                        lessonInfo.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_round_local_cafe_24, 0, 0, 0
                        )
                        lessonInfo.visibility = View.VISIBLE
                    }
                    else -> {
                        lessonInfo.visibility = View.GONE
                    }
                }
            } else {
                lessonInfo.visibility = View.GONE
            }
        }


        private fun setLessonType(enabled: Boolean) {
            val type = if (adapter.showGroup)
                lesson.type.toUpperCase() + "  " + lesson.group.title
            else
                lesson.type.toUpperCase()

            lessonType.setTextColor(
                if (enabled)
                    (if (lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
                else
                    adapter.disabledColor
            )
            lessonType.setText(type, TextView.BufferType.NORMAL)
            lessonType.isEnabled = enabled
        }

        private fun setAuditoriums(enabled: Boolean) {
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
                    val audTitle = parseAuditoriumTitle(auditorium.title.toLowerCase())
                    val color = parseAuditoriumColor(auditorium.color)
                    if (color != null) {
                        auditoriums
                            .append(
                                audTitle, ForegroundColorSpan(color),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            .append(
                                ", ", ForegroundColorSpan(color),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                    } else {
                        auditoriums
                            .append(audTitle)
                            .append(", ")
                    }
                }
                val lastAuditorium = lesson.auditoriums.last()
                val audTitle = parseAuditoriumTitle(lastAuditorium.title.toLowerCase())
                val color = parseAuditoriumColor(lastAuditorium.color)
                if (color != null) {
                    auditoriums.append(
                        audTitle,
                        ForegroundColorSpan(color),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    auditoriums.append(audTitle)
                }
            } else {
                for (i in 0 until lesson.auditoriums.size - 1) {
                    val audTitle = parseAuditoriumTitle(lesson.auditoriums[i].title.toLowerCase())
                    auditoriums
                        .append(
                            audTitle,
                            ForegroundColorSpan(adapter.disabledColor),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        .append(
                            ", ",
                            ForegroundColorSpan(adapter.disabledColor),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                }
                val audTitle = HtmlCompat.fromHtml(
                    lesson.auditoriums.last().title.toLowerCase(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                auditoriums.append(
                    audTitle,
                    ForegroundColorSpan(adapter.disabledColor),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            lessonAuditoriums.setText(auditoriums, TextView.BufferType.NORMAL)
            auditoriums.clear()
        }

        private fun parseAuditoriumTitle(title: String): SpannableString {
            return SpannableString(HtmlCompat.fromHtml(
                title,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )).apply {
                getSpans<URLSpan>().forEach { removeSpan(it) }
                getSpans<UnderlineSpan>().forEach { removeSpan(it) }
                getSpans<ClickableSpan>().forEach { removeSpan(it) }
            }
        }

        private fun parseAuditoriumColor(colorString: String): Int? {
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
            if (adapter.nightMode) {
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

        private fun setTitle(enabled: Boolean) {
            val title = lesson.title
            if (title.isEmpty()) {
                lessonTitle.visibility = View.GONE
            } else {
                lessonTitle.visibility = View.VISIBLE
            }
            lessonTitle.setText(title, TextView.BufferType.NORMAL)
            lessonTitle.isEnabled = enabled
        }

        private fun setTeachers(enabled: Boolean) {
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

    }
}