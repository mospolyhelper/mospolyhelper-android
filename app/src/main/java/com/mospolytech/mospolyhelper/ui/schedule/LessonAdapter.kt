package com.mospolytech.mospolyhelper.ui.schedule

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.*
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.schedule.LessonLabelRepository
import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.utils.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class LessonAdapter(
    var dailySchedule: List<Lesson>,
    val lessonLabelRepository: LessonLabelRepository,
    val deadlinesRepository: DeadlinesRepository,
    var filter: Schedule.Filter,
    var date: LocalDate,
    var showGroup: Boolean,
    var nightMode: Boolean,
    var disabledColor: Int,
    var headColor: Int,
    var headCurrentColor: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val lessonTypeColors = listOf(
            0xffeb4141.toInt(),   // Exam, Credit,..
            0xff29b6f6.toInt()    // Other
        )
        const val VIEW_TYPE_NORMAL_SINGLE = 0
        const val VIEW_TYPE_NORMAL_TOP = 1
        const val VIEW_TYPE_NORMAL_MIDDLE = 2
        const val VIEW_TYPE_NORMAL_BOTTOM = 3
        const val VIEW_TYPE_EMPTY = 4
        const val VIEW_TYPE_INFO = 5

    }

    private var itemCount = 0
    var currLessonOrder = 0

    val lessonClick: Event2<Lesson, LocalDate> = Action2()

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

    override fun getItemViewType(position: Int): Int {
        val lesson = dailySchedule[position]
        return if (lesson.isEmpty) {
            if (lesson.order == -1) {
                VIEW_TYPE_INFO
            } else {
                VIEW_TYPE_EMPTY
            }
        } else {
            val prevEqual = position != 0 && dailySchedule[position - 1].equalsTime(lesson)
            val nextEqual = (position != itemCount - 1) && dailySchedule[position + 1].equalsTime(lesson)
            when {
                prevEqual && nextEqual -> VIEW_TYPE_NORMAL_MIDDLE
                prevEqual -> VIEW_TYPE_NORMAL_BOTTOM
                nextEqual -> VIEW_TYPE_NORMAL_TOP
                else -> VIEW_TYPE_NORMAL_SINGLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL_SINGLE,
            VIEW_TYPE_NORMAL_TOP,
            VIEW_TYPE_NORMAL_BOTTOM,
            VIEW_TYPE_NORMAL_MIDDLE -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_lesson, parent, false)
                ViewHolder(view, viewType) { lesson, date ->
                    (lessonClick as Action2).invoke(lesson, date) }
            }
            VIEW_TYPE_EMPTY -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_lesson_empty, parent, false)
                ViewHolderEmpty(view)
            }
            VIEW_TYPE_INFO -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_lesson_info, parent, false)
                ViewHolderInfo(view)
            }
            else -> onCreateViewHolder(parent, VIEW_TYPE_NORMAL_SINGLE)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            VIEW_TYPE_NORMAL_SINGLE,
            VIEW_TYPE_NORMAL_TOP,
            VIEW_TYPE_NORMAL_BOTTOM,
            VIEW_TYPE_NORMAL_MIDDLE -> {
                (viewHolder as ViewHolder).bind(this)
            }
            VIEW_TYPE_EMPTY -> {
                (viewHolder as ViewHolderEmpty).bind(this)
            }
            VIEW_TYPE_INFO -> {
                (viewHolder as ViewHolderInfo).bind(this)
            }
        }
    }

    class ViewHolder(
        val view: View,
        preLoadViewType: Int,
        onLessonClick: (Lesson, LocalDate) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private lateinit var adapter: LessonAdapter
        private val lessonTitle = view.findViewById<TextView>(R.id.text_schedule_title)!!
        private val lessonTime = view.findViewById<TextView>(R.id.text_lesson_time)!!
        private val lessonTeachers = view.findViewById<TextView>(R.id.text_lesson_teachers_title)!!
        private val lessonAuditoriums = view.findViewById<TextView>(R.id.text_lesson_auditoriums_title)!!
        private val lessonPlace = view.findViewById<LinearLayout>(R.id.layout_lesson)!!
        private var lesson: Lesson = Lesson.getEmpty(0)
        private val hasTime: Boolean

        init {
            lessonPlace.setSafeOnClickListener { onLessonClick(lesson, adapter.date) }
            setBackground(preLoadViewType)
            hasTime = preLoadViewType == VIEW_TYPE_NORMAL_SINGLE || preLoadViewType == VIEW_TYPE_NORMAL_TOP
            if (hasTime) {
                lessonTime.visibility = View.VISIBLE
            } else {
                lessonTime.visibility = View.GONE
            }
        }

        private fun setBackground(viewType: Int) {
            when (viewType) {
                VIEW_TYPE_NORMAL_SINGLE ->  {
                    val dp8 = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        view.resources.displayMetrics
                    ).toInt()
                    val lessonPlaceParams = (lessonPlace.layoutParams as FrameLayout.LayoutParams)
                    lessonPlaceParams.topMargin = dp8
                    lessonPlaceParams.bottomMargin = dp8
                    lessonPlace.layoutParams = lessonPlaceParams

                    lessonPlace.setBackgroundResource(R.drawable.shape_lesson)
                }
                VIEW_TYPE_NORMAL_TOP ->  {
                    val dp8 = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        view.resources.displayMetrics
                    ).toInt()
                    val lessonPlaceParams = (lessonPlace.layoutParams as FrameLayout.LayoutParams)
                    lessonPlaceParams.topMargin = dp8
                    lessonPlaceParams.bottomMargin = 0
                    lessonPlace.layoutParams = lessonPlaceParams

                    lessonPlace.setBackgroundResource(R.drawable.shape_lesson_top)
                }
                VIEW_TYPE_NORMAL_BOTTOM ->  {
                    val dp8 = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        view.resources.displayMetrics
                    ).toInt()
                    val lessonPlaceParams = (lessonPlace.layoutParams as FrameLayout.LayoutParams)
                    lessonPlaceParams.topMargin = 0
                    lessonPlaceParams.bottomMargin = dp8
                    lessonPlace.layoutParams = lessonPlaceParams

                    lessonPlace.setBackgroundResource(R.drawable.shape_lesson_bottom)
                }
                VIEW_TYPE_NORMAL_MIDDLE -> {
                    val lessonPlaceParams = (lessonPlace.layoutParams as FrameLayout.LayoutParams)
                    lessonPlaceParams.topMargin = 0
                    lessonPlaceParams.bottomMargin = 0
                    lessonPlace.layoutParams = lessonPlaceParams

                    lessonPlace.setBackgroundResource(R.drawable.shape_lesson_middle)
                }
            }
        }

        // ViewHolder class is not inner because recycler views have common pool
        fun bind(adapter: LessonAdapter) {
            this.adapter = adapter
            lesson = adapter.dailySchedule[adapterPosition]


            val enabledFrom =
                lesson.dateFrom <= adapter.date || adapter.filter.dateFilter != Schedule.Filter.DateFilter.Desaturate
            val enabledTo =
                lesson.dateTo >= adapter.date || adapter.filter.dateFilter != Schedule.Filter.DateFilter.Desaturate
            val enabled =
                if (lesson.isImportant) enabledTo else enabledFrom && enabledTo

            if (adapterPosition == adapter.itemCount - 1) {
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

            if (hasTime) setTime(enabled)
            setLessonTitleAndFeatures(enabled)
            setAuditoriums(enabled)
            setTeachers(enabled)
            lessonPlace.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add("Добавить дедлайн")
                menu.add("Добавить метку на этот день")
                menu.add("Добавить метку на всё время")
            }
        }

        private fun setTime(enabled: Boolean) {
            val (timeStart, timeEnd) = lesson.time
            //lessonTime.isEnabled = enabled
            lessonTime.text = "$timeStart - $timeEnd, ${lesson.order + 1}-е занятие"
        }


        private fun SpannableStringBuilder.spansAppend(text: String, flags: Int, vararg spans: Any) {
            val start = length
            append(text)
            val length = length
            for (span in spans) {
                setSpan(span, start, length, flags)
            }
        }

        private fun getDeadlinesEnd(count: Int): String {
            val lastNumber = count % 10
            return when {
                count in 11L..14 -> "ов"
                lastNumber == 1 -> ""
                lastNumber in 2..4 -> "а"
                else -> "ов"
            }
        }

        private fun setLessonTitleAndFeatures(enabled: Boolean) {
            val builder = SpannableStringBuilder()
            val color = if (enabled)
                (if (lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
            else
                adapter.disabledColor

            val sp17 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                17f,
                view.resources.displayMetrics
            ).toInt()

            builder.append(lesson.title, AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.append("\u00A0 ", AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            builder.append("\u00A0\u00A0\u00A0${lesson.type.capitalize()}\u00A0\u00A0\u00A0",
                RoundedBackgroundSpan(color),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val colors = listOf(
                0xff9b59b6.toInt(),
                0xff2ecc71.toInt(),
                0xffe67e22.toInt(),
                0xff7f8c8d.toInt()
            )

            if (adapter.showGroup) {
                builder.append("\u00A0 ", AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.append(
                    "\u00A0\u00A0\u00A0${lesson.group.title}\u00A0\u00A0\u00A0",
                    RoundedBackgroundSpan(colors[Random.nextInt(colors.size)]),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (lesson.title == "Проектное макетирование") {
                val q = 1
            }
            val deadlines = adapter.deadlinesRepository.getDeadlinesCurrent().value

            if (deadlines != null) {
                val count = deadlines.count { it.name.contains(lesson.title, true) }
                if (count != 0) {
                    builder.append("\u00A0 ", AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    builder.append(
                        "\u00A0\u00A0\u00A0${count} дедлайн${getDeadlinesEnd(count)}\u00A0\u00A0\u00A0",
                        RoundedBackgroundSpan(colors[Random.nextInt(colors.size)]),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            val labels = adapter.lessonLabelRepository.getLabels(lesson, adapter.date)


            // TODO delete random
            if (Random.nextBoolean() && labels.first.isNotEmpty()) {
                for (label in labels.first) {
                    if (Random.nextBoolean()) {
                        builder.append("\u00A0 ", AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        builder.append(
                            "\u00A0\u00A0$label\u00A0\u00A0\u00A0",
                            RoundedBackgroundSpan(colors[Random.nextInt(colors.size)]),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
            if (Random.nextBoolean() && Random.nextBoolean() && labels.second.isNotEmpty()) {
                for (label in labels.second) {
                    if (Random.nextBoolean()) {
                        builder.append("\u00A0 ", AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        builder.append(
                            "\u00A0\u00A0$label\u00A0\u00A0\u00A0",
                            RoundedBackgroundSpan(colors[Random.nextInt(colors.size)]),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }

            builder.append("\u00A0", AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            lessonTitle.text = builder
            lessonTitle.isEnabled = enabled
        }

        private fun setAuditoriums(enabled: Boolean) {
            lessonAuditoriums.isEnabled = enabled
            if (lesson.auditoriums.isEmpty()) {
                lessonAuditoriums.visibility = View.GONE
            } else {
                lessonAuditoriums.visibility = View.VISIBLE
                lessonAuditoriums.text = lesson.auditoriums.joinToString(separator = ", ") {
                    parseAuditoriumTitle(it.title)
                }
            }
        }

        private fun parseAuditoriumTitle(title: String): String {
            val str = SpannableString(
                HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            ).toString()
            val index = str.indexOfFirst { it.isLetterOrDigit() }
            return if (index == -1) str else str.substring(index)

//                .apply {
//                getSpans<URLSpan>().forEach { removeSpan(it) }
//                getSpans<UnderlineSpan>().forEach { removeSpan(it) }
//                getSpans<ClickableSpan>().forEach { removeSpan(it) }
//            }
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

    class ViewHolderEmpty(view: View): RecyclerView.ViewHolder(view) {
        fun bind(adapter: LessonAdapter) {
            val lesson = adapter.dailySchedule[adapterPosition]
            (itemView as TextView).text = "${lesson.time.first}, ${lesson.order + 1}-е занятие"
        }
    }

    class ViewHolderInfo(view: View): RecyclerView.ViewHolder(view) {
        private lateinit var adapter: LessonAdapter
        private val lessonInfo: TextView = (itemView as TextView)

        fun bind(adapter: LessonAdapter) {
            this.adapter = adapter
            setInfo()
        }

        private fun setInfo() {
            var info: String
            val prevLesson = adapter.dailySchedule[adapterPosition - 1]
            val nextLesson = adapter.dailySchedule[adapterPosition + 1]
            if (prevLesson.order == 2) {
                info = "большой перерыв на 40 минут"
                lessonInfo.text = info
                lessonInfo.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_round_local_cafe_24, 0, 0, 0
                )
            } else {
                val totalMinutes = prevLesson.localTime.second.until(
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
                val id = when {
                    totalMinutes < 180 -> R.drawable.ic_baseline_fastfood_24
                    totalMinutes < 270 -> R.drawable.ic_round_sports_esports_24
                    else -> R.drawable.ic_round_sports_volleyball_24
                }
                lessonInfo.setCompoundDrawablesWithIntrinsicBounds(
                    id, 0, 0, 0
                )
            }
        }
    }

}