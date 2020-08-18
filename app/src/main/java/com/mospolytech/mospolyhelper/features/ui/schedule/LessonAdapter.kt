package com.mospolytech.mospolyhelper.features.ui.schedule

import android.graphics.Typeface
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
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonLabelRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonLabelKey
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.utils.*
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class LessonAdapter(
    var dailySchedule: List<Lesson>,
    var map: List<Boolean>,
    val labels: Map<LessonLabelKey, Set<String>>,
    val deadlines: Map<String, List<Deadline>>,
    var filter: Schedule.Filter,
    var date: LocalDate,
    var showGroup: Boolean,
    var disabledColor: Int,
    var headColor: Int,
    var headCurrentColor: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val lessonTypeColors = listOf(
            0xffe74c3c.toInt(),   // Exam, Credit,..
            0xff2e86c1.toInt()    // Other
        )
        const val VIEW_TYPE_NORMAL_SINGLE = 0
        const val VIEW_TYPE_NORMAL_TOP = 1
        const val VIEW_TYPE_NORMAL_MIDDLE = 2
        const val VIEW_TYPE_NORMAL_BOTTOM = 3
        const val VIEW_TYPE_TITLE = 4
        const val VIEW_TYPE_EMPTY = 5
        const val VIEW_TYPE_INFO = 6
    }

    val lessonClick: Event3<Lesson, LocalDate, List<View>> = Action3()

    override fun getItemCount() = dailySchedule.size

    var currentOrder = -2
    var currentOrderIsStarted = false
    var cleared = false

    fun updateTime(order: Int, isStarted: Boolean, groupIsEvening: Boolean, updatePreviousOrder: Boolean) {
        // If not today then clear time and return
        if (date != LocalDate.now()) {
            clearTime()
            return
        }
        currentOrder = getCurrOrder(order)
        // If lessons end then clear time and return
        if (currentOrder == 8) {
            clearTime()
            return
        }
        currentOrderIsStarted = if (currentOrder == order) isStarted else false

        // If current date changed in settings and fragment will not update
        if (cleared) {
            cleared = false
        }
        val prevOrder = order - 1
        val size = dailySchedule
        for (lesson in dailySchedule.withIndex()) {
            if (
                (lesson.value.order == currentOrder ||
                        updatePreviousOrder &&
                        lesson.value.order == prevOrder) &&
                lesson.value.group.isEvening == groupIsEvening
            ) {
                notifyItemChanged(lesson.index)
            }
        }
    }

    private fun clearTime() {
        if (cleared) {
            return
        }
        cleared = true
        currentOrder = -2
        currentOrderIsStarted = false
        notifyDataSetChanged()
    }

    private fun getCurrOrder(order: Int): Int {
        if (order == 8) {
            return 8
        }
        var currOrder = order
        while (!map[currOrder]) {
            if (++currOrder == map.size) {
                return 8
            }
        }
        return currOrder
    }

    fun update(
        dailySchedule: List<Lesson>,
        map: List<Boolean>,
        scheduleFilter: Schedule.Filter,
        date: LocalDate,
        showGroup: Boolean
    ) {
        this.dailySchedule = dailySchedule
        this.map = map
        this.showGroup = showGroup
        this.date = date
        this.filter = scheduleFilter
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val lesson = dailySchedule[position]
        return if (lesson.isEmpty) {
            if (lesson.order == -1) {
                if (position == 0) VIEW_TYPE_TITLE else VIEW_TYPE_INFO
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
                ViewHolder(view, viewType) { lesson, date, views ->
                    (lessonClick as Action3).invoke(lesson, date, views) }
            }
            VIEW_TYPE_TITLE -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_lesson_title, parent, false)
                ViewHolderTitle(view)
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
            VIEW_TYPE_TITLE -> {
                (viewHolder as ViewHolderTitle).bind(this)
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
        onLessonClick: (Lesson, LocalDate, List<View>) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private lateinit var adapter: LessonAdapter
        private val lessonTitle = view.findViewById<TextView>(R.id.text_schedule_title)!!
        private val lessonTime = view.findViewById<TextView>(R.id.text_lesson_time)!!
        private val lessonTeachers = view.findViewById<TextView>(R.id.text_lesson_teachers)!!
        private val lessonAuditoriums = view.findViewById<TextView>(R.id.text_lesson_auditoriums)!!
        private val lessonPlace = view.findViewById<LinearLayout>(R.id.layout_lesson)!!
        private var lesson: Lesson = Lesson.getEmpty(0)
        private val hasTime: Boolean
        //private var hasLeftTimeLabel: Boolean = false
        //private var isCurrent: Boolean = false

        init {
            lessonPlace.setSafeOnClickListener {
                lessonPlace.transitionName = lesson.hashCode().toString()
                lessonTitle.transitionName = lessonPlace.transitionName + "Title"
                lessonTeachers.transitionName = lessonPlace.transitionName + "Teachers"
                lessonAuditoriums.transitionName = lessonPlace.transitionName + "Auditoriums"
                onLessonClick(lesson, adapter.date, listOf(lessonPlace, lessonTitle, lessonTeachers, lessonAuditoriums))
            }
            setBackground(preLoadViewType)
            hasTime = setHasTime(preLoadViewType)
            setContextMenu()
        }

        private fun setHasTime(viewType: Int): Boolean {
            val hasTime = viewType == VIEW_TYPE_NORMAL_SINGLE || viewType == VIEW_TYPE_NORMAL_TOP
            if (hasTime) {
                lessonTime.visibility = View.VISIBLE
            } else {
                lessonTime.visibility = View.GONE
            }
            return hasTime
        }

        private fun setContextMenu() {
            lessonPlace.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add("Добавить дедлайн")
                menu.add("Добавить метку на этот день")
                menu.add("Добавить метку на всё время")
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


            setBottomPadding()
            //checkCurrentTime()
            if (hasTime) setTime(enabled)
            setLessonTitleAndFeatures(enabled)
            setAuditoriums(enabled)
            setTeachers(enabled)
        }

        private fun setBottomPadding() {
            var paddingBottom = 0
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
                    paddingBottom = (actionBarHeight * 1.6).toInt()
                }
            }
            view.setPadding(0, 0,0, paddingBottom)
        }

//        private fun checkCurrentTime() {
//            if (adapter.date == LocalDate.now()) {
//                val (currentOrder, isStarted) = Lesson.getOrder(LocalTime.now(), lesson.group.isEvening)
//                if (lesson.order == currentOrder) {
//                    isCurrent = isStarted
//                    hasLeftTimeLabel = false
//                } else {
//                    if (currentOrder < lesson.order) {
//                        var lessonWithCurrentOrderBeforeExist = false
//                        var order = lesson.order - 1
//                        while (currentOrder != order) {
//                            if (adapter.map[order]) {
//                                lessonWithCurrentOrderBeforeExist = true
//                                break
//                            }
//                            order--
//                        }
//                        if (lessonWithCurrentOrderBeforeExist) {
//                            isCurrent = false
//                            hasLeftTimeLabel = false
//                        } else {
//                            isCurrent = false
//                            hasLeftTimeLabel = true
//                        }
//                    } else {
//                        isCurrent = false
//                        hasLeftTimeLabel = false
//                    }
//                }
//            } else {
//                isCurrent = false
//                hasLeftTimeLabel = false
//            }
//        }

        private fun setTime(enabled: Boolean) {
            if (lesson.order == -1) {
                val q = 1
            }
            val (timeStart, timeEnd) = lesson.time
            lessonTime.text = "$timeStart - $timeEnd,  ${lesson.order + 1}-е занятие"
        }


        private fun SpannableStringBuilder.appendAny(text: String, flags: Int, vararg spans: Any) {
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
            val color2 = if (enabled)
                (if (lesson.isImportant) lessonTypeColors[0] else lessonTypeColors[1])
            else
                adapter.disabledColor

            val color = view.context.getColor(R.color.chipColor)

            val sp17 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                17f,
                view.resources.displayMetrics
            ).toInt()

            builder.append(lesson.title)
            builder.append("  ")

            builder.appendAny(
                "\u00A0",
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                RoundedBackgroundSpan(color2, height = sp17, text = lesson.type.toLowerCase()),
                StyleSpan(Typeface.BOLD)
            )

            if (adapter.currentOrderIsStarted && lesson.order == adapter.currentOrder) {
                val time = getTime(lesson.localTime.first.until(LocalTime.now(), ChronoUnit.MINUTES), true)
                builder.append("  ")
                builder.appendAny(
                    "\u00A0",
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    RoundedBackgroundSpan(0xff229954.toInt(), height = sp17, text = "Идёт  ${time.replace(" ", "  ").toLowerCase()}"),
                    StyleSpan(Typeface.BOLD)
                )
            }
            if (!adapter.currentOrderIsStarted && lesson.order == adapter.currentOrder) {
                val time = getTime(LocalTime.now().until(lesson.localTime.first, ChronoUnit.MINUTES), false)
                builder.append("  ")
                builder.appendAny(
                    "\u00A0",
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    RoundedBackgroundSpan(0xffe67e22.toInt(), height = sp17, text = "${time.replace(" ", "  ").toLowerCase()}  до  начала"),
                    StyleSpan(Typeface.BOLD)
                )
            }


            if (adapter.showGroup) {
                builder.append("  ")
                builder.appendAny(
                    "\u00A0",
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    RoundedBackgroundSpan(color, height = sp17, text = lesson.group.title.toLowerCase()),
                    StyleSpan(Typeface.BOLD)
                )
            }
            var deadlinesCount = Random.nextInt(4)
            if (deadlinesCount != 0) {
                deadlinesCount = Random.nextInt(4)
            }
            if (deadlinesCount != 0) {
                deadlinesCount = Random.nextInt(4)
            }

            val count = deadlinesCount
            if (count != 0) {
                builder.append("  ")
                builder.appendAny(
                    "\u00A0",
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    RoundedBackgroundSpan(color, height = sp17, text = "$count  дедлайн${getDeadlinesEnd(count)}"),
                    StyleSpan(Typeface.BOLD)
                )
            }

            val labelsAllDates = adapter.labels[LessonLabelKey.from(lesson, adapter.date, true)]
            val labelsOneDates = adapter.labels[LessonLabelKey.from(lesson, adapter.date, false)]


            // TODO delete random
            if (labelsAllDates!= null && Random.nextBoolean() && Random.nextBoolean() && labelsAllDates.isNotEmpty()) {
                for (label in labelsAllDates) {
                    if (Random.nextBoolean()) {
                        builder.append("  ", AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        builder.appendAny(
                            "\u00A0",
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                            RoundedBackgroundSpan(color, height = sp17, text = label.toLowerCase()),
                            StyleSpan(Typeface.BOLD)
                        )
                    }
                }
            }
            if (labelsOneDates != null && Random.nextBoolean() && Random.nextBoolean() && labelsOneDates.isNotEmpty()) {
                for (label in labelsOneDates) {
                    if (Random.nextBoolean()) {
                        builder.append("  ", AbsoluteSizeSpan(sp17), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        builder.appendAny(
                            "\u00A0",
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                            RoundedBackgroundSpan(color, height = sp17, text = label.toLowerCase()),
                            StyleSpan(Typeface.BOLD)
                        )
                    }
                }
            }
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

    class ViewHolderTitle(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM")
        }
        private val textView = (view as TextView)

        fun bind(adapter: LessonAdapter) {
            textView.text = adapter.date.format(dateFormatter).capitalize()
        }
    }

    class ViewHolderEmpty(view: View): RecyclerView.ViewHolder(view) {
        private val textView = (view as TextView)

        fun bind(adapter: LessonAdapter) {
            val lesson = adapter.dailySchedule[adapterPosition]
            if (lesson.order == -1) {
                val q = 1
            }
            textView.text = "${lesson.time.first},  ${lesson.order + 1}-е занятие"
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

fun getTime(totalMinutes: Long, isGenitive: Boolean): String {
    var timeLeft = StringBuilder()
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
    if (windowTimeHours != 0L) {
        timeLeft.append("$windowTimeHours час$endingHours")
    }
    if (windowTimeMinutes != 0L) {
        if (windowTimeHours != 0L) {
            timeLeft.append(" ")
        }
        // *1 минута .. *2, *3, *4 минуты .. *5, *6, *7, *8, *9, *0 минут .. искл. - 11 - 14
        val lastNumberOfMinutes = windowTimeMinutes % 10
        val endingMinutes = when {
            windowTimeMinutes in 11L..14L -> ""
            lastNumberOfMinutes == 1L -> if (isGenitive) "у" else "а"
            lastNumberOfMinutes in 2L..4L -> "ы"
            else -> ""
        }
        timeLeft.append("$windowTimeMinutes минут$endingMinutes")
    }

    if (windowTimeHours == 0L && windowTimeMinutes == 0L) {
        timeLeft.append("менее минуты")
    }

    return timeLeft.toString()
}