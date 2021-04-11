package com.mospolytech.mospolyhelper.features.ui.schedule

import android.content.res.ColorStateList
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemLessonBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonEmptyBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonInfoBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonInfoObjectBinding
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Group
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.Tag
import com.mospolytech.mospolyhelper.features.widget.NoTouchRecyclerView
import com.mospolytech.mospolyhelper.utils.*
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class LessonAdapter(
    var dailySchedule: List<Lesson>,
    var map: List<Boolean>,
    val tags: Map<LessonTagKey, List<Tag>>,
    val deadlines: Map<String, List<Deadline>>,
    var date: LocalDate,
    var showGroups: Boolean,
    var showTeachers: Boolean,
    currentLesson: Pair<Lesson.CurrentLesson, Lesson.CurrentLesson>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        val orderColors = listOf(
            0xffff764d.toInt(), // 1
            0xffffccbc.toInt(), // 2
            0xffbbdefb.toInt(), // 3
            0xff55aef6.toInt(), // 4
            0xff1359a0.toInt(), // 5
            0xff302e88.toInt(), // 6
            0xff310e84.toInt()  // 7
        )

        const val VIEW_TYPE_NORMAL = 0
        const val VIEW_TYPE_NORMAL_TOP = 1
        const val VIEW_TYPE_NORMAL_BOTTOM = 2
        const val VIEW_TYPE_NORMAL_MIDDLE = 3
        const val VIEW_TYPE_TITLE = 4
        const val VIEW_TYPE_EMPTY = 5
        const val VIEW_TYPE_INFO = 6

        private val dateFormatter = DateTimeFormatter.ofPattern("d MMM")
    }

    val lessonClick: Event3<Lesson, LocalDate, List<View>> = Action3()

    override fun getItemCount() = dailySchedule.size

    private var currentOrder = -2
    private var currentOrderIsStarted = false
    private var cleared = true

    private var currentOrderEvening = -2
    private var currentOrderIsStartedEvening = false
    private var clearedEvening = true

    init {
        calculateCurrent(currentLesson.first)
        calculateCurrentEvening(currentLesson.second)
    }

    private fun calculateCurrent(currentLesson: Lesson.CurrentLesson) {
        if (date != LocalDate.now()) {
            clearTime()
            return
        }
        currentOrder = getCurrOrder(currentLesson.order)
        // If lessons end then clear time and return
        if (currentOrder == 8) {
            clearTime()
            return
        }
        currentOrderIsStarted = currentOrder == currentLesson.order && currentLesson.isStarted

        // If current date changed in settings and fragment will not update
        if (cleared) {
            cleared = false
        }
    }

    private fun calculateCurrentEvening(currentLesson: Lesson.CurrentLesson) {
        if (date != LocalDate.now()) {
            clearTimeEvening()
            return
        }
        currentOrderEvening = getCurrOrder(currentLesson.order)
        // If lessons end then clear time and return
        if (currentOrderEvening == 8) {
            clearTime()
            return
        }
        currentOrderIsStartedEvening = currentOrderEvening == currentLesson.order && currentLesson.isStarted

        // If current date changed in settings and fragment will not update
        if (clearedEvening) {
            clearedEvening = false
        }
    }

    fun updateTime(currentLesson: Pair<Lesson.CurrentLesson, Lesson.CurrentLesson>, updatePreviousOrder: Boolean) {
        val currLesson = currentLesson.first
        calculateCurrent(currLesson)
        val prevOrder = currLesson.order - 1

        val currLessonEvening = currentLesson.second
        calculateCurrentEvening(currLessonEvening )
        val prevOrderEvening = currLessonEvening.order - 1


        for (lesson in dailySchedule.withIndex()) {
            if (date !in lesson.value.dateFrom..lesson.value.dateTo) continue
            if (
                lesson.value.groupIsEvening == currLesson.isEvening &&
                (lesson.value.order == currentOrder ||
                        updatePreviousOrder && lesson.value.order == prevOrder)
            ) {
                notifyItemChanged(lesson.index)
            }
            if (
                lesson.value.groupIsEvening == currLessonEvening.isEvening &&
                (lesson.value.order == currentOrder ||
                        updatePreviousOrder && lesson.value.order == prevOrderEvening)
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

    private fun clearTimeEvening() {
        if (clearedEvening) {
            return
        }
        clearedEvening  = true
        currentOrderEvening = -2
        currentOrderIsStartedEvening = false
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
        date: LocalDate,
        showGroups: Boolean,
        showTeachers: Boolean,
        currentLesson: Pair<Lesson.CurrentLesson, Lesson.CurrentLesson>
    ) {
        this.dailySchedule = dailySchedule
        this.map = map
        this.showGroups = showGroups
        this.showTeachers = showTeachers
        this.date = date
        calculateCurrent(currentLesson.first)
        calculateCurrentEvening(currentLesson.second)
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
                nextEqual -> VIEW_TYPE_NORMAL_TOP
                prevEqual -> VIEW_TYPE_NORMAL_BOTTOM
                else -> VIEW_TYPE_NORMAL
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL,
            VIEW_TYPE_NORMAL_TOP,
            VIEW_TYPE_NORMAL_BOTTOM,
            VIEW_TYPE_NORMAL_MIDDLE -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_lesson, parent, false)
                ViewHolder(view, viewType)
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
            else -> onCreateViewHolder(parent, VIEW_TYPE_NORMAL)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            VIEW_TYPE_NORMAL,
            VIEW_TYPE_NORMAL_TOP,
            VIEW_TYPE_NORMAL_BOTTOM,
            VIEW_TYPE_NORMAL_MIDDLE -> {
                val lesson = dailySchedule[position]
                val tags = tags[LessonTagKey.fromLesson(lesson)] ?: emptyList()
                (viewHolder as ViewHolder).bind(this, lesson, tags) { lesson, date, views ->
                    (lessonClick as Action3).invoke(lesson, date, views) }
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
        preViewType: Int
    ) : RecyclerView.ViewHolder(view) {
        private val disabledColor = view.context.getColor(R.color.textSecondaryDisabled)
        private val chipTextColor = view.context.getColor(R.color.scheduleLessonChipText)
        private val chipColor = view.context.getColor(R.color.scheduleLessonChip)
        private val viewBinding by viewBinding(ItemLessonBinding::bind)
        private val hasTime: Boolean
        //private var hasLeftTimeLabel: Boolean = false
        //private var isCurrent: Boolean = false

        init {
            viewBinding.tags.layoutManager = FlexboxLayoutManager(view.context).apply {
                recycleChildrenOnDetach = true
            }
            setBackground(preViewType)
            hasTime = setHasTime(preViewType)
        }

        private fun setHasTime(preViewType: Int): Boolean {
            val hasTime = preViewType != VIEW_TYPE_NORMAL_BOTTOM && preViewType != VIEW_TYPE_NORMAL_MIDDLE
            if (hasTime) {
                viewBinding.textLessonTime.visibility = View.VISIBLE
                viewBinding.textLessonCurrent.visibility = View.VISIBLE
            } else {
                viewBinding.textLessonTime.visibility = View.GONE
                viewBinding.textLessonCurrent.visibility = View.GONE
            }
            return hasTime
        }

        private fun setBackground(preViewType: Int) {
            val lessonPlaceParams = (viewBinding.layoutLesson.layoutParams as LinearLayout.LayoutParams)
            when(preViewType) {
                VIEW_TYPE_NORMAL_MIDDLE -> {
                    lessonPlaceParams.topMargin = 0
                    lessonPlaceParams.bottomMargin = 0
                }
                VIEW_TYPE_NORMAL_BOTTOM ->  {
                    val dp8 = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        view.resources.displayMetrics
                    ).toInt()
                    lessonPlaceParams.topMargin = 0
                    lessonPlaceParams.bottomMargin = dp8
                }
                VIEW_TYPE_NORMAL_TOP ->  {
                    val dpTop = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        3f,
                        view.resources.displayMetrics
                    ).toInt()
                    lessonPlaceParams.topMargin = 0//dpTop
                    lessonPlaceParams.bottomMargin = 0
                }
                else ->  {
                    val dp8 = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        view.resources.displayMetrics
                    ).toInt()
                    val dpTop = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        3f,
                        view.resources.displayMetrics
                    ).toInt()
                    lessonPlaceParams.topMargin = 0//dpTop
                    lessonPlaceParams.bottomMargin = dp8
                }
            }
            viewBinding.layoutLesson.layoutParams = lessonPlaceParams
        }

        // ViewHolder class is not inner because recycler views have common pool
        fun bind(adapter: LessonAdapter, lesson: Lesson, tags: List<Tag>, onLessonClick: (Lesson, LocalDate, List<View>) -> Unit) {
            viewBinding.layoutLesson.setSafeOnClickListener {
                onLessonClick(lesson, adapter.date, emptyList())
            }
            val enabled = adapter.date in lesson.dateFrom..lesson.dateTo

            setBottomPadding(adapter)
            if (hasTime) setTime(lesson, adapter)
            setLessonTitleAndFeatures(lesson, enabled)
            setAuditoriums(lesson, enabled)
            setTeachers(lesson, adapter, enabled)
            setGroups(lesson, adapter, enabled)
            setTags(tags)
        }

        private fun setBottomPadding(adapter: LessonAdapter) {
            var paddingBottom = 0
            if (bindingAdapterPosition == adapter.itemCount - 1) {
                val tv = TypedValue()
                if (viewBinding.layoutLesson.context.theme.resolveAttribute(
                        android.R.attr.actionBarSize,
                        tv,
                        true
                    )
                ) {
                    val actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data,
                        viewBinding.layoutLesson.context.resources.displayMetrics
                    )
                    paddingBottom = (actionBarHeight * 0.8).toInt()
                }
            }
            val dp8 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12f,
                view.resources.displayMetrics
            ).toInt()
            val dp3 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                3f,
                view.resources.displayMetrics
            ).toInt()
            view.setPadding(view.paddingLeft, dp3,view.paddingRight, paddingBottom)
        }

        private fun setTime(lesson: Lesson, adapter: LessonAdapter) {

            val currentOrder: Int
            val currentLessonIsStarted: Boolean
            if (lesson.groupIsEvening) {
                currentOrder = adapter.currentOrderEvening
                currentLessonIsStarted = adapter.currentOrderIsStartedEvening
            } else {
                currentOrder = adapter.currentOrder
                currentLessonIsStarted = adapter.currentOrderIsStarted
            }

            // Current lessons label
            if (lesson.order == currentOrder) {
                val currentLessonText: String
                if (currentLessonIsStarted) {
                    val time = getTime((lesson.localTime.first.until(LocalTime.now(), ChronoUnit.SECONDS) / 60f).roundToLong(), true)
                    currentLessonText = "Идёт ${time.toLowerCase()}"
                } else {
                    val time = getTime((LocalTime.now().until(lesson.localTime.first, ChronoUnit.SECONDS) / 60f).roundToLong(), false)
                    currentLessonText = "До начала ${time.toLowerCase()}"
                }

                viewBinding.textLessonCurrent.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_current_lesson, 0, 0, 0)
                viewBinding.textLessonCurrent.text = currentLessonText
                viewBinding.textLessonCurrent.visibility = View.VISIBLE
            } else {
                viewBinding.textLessonCurrent.visibility = View.GONE
            }
            viewBinding.textLessonOrder.text = (lesson.order + 1).toString()

            var orderColor: Int
            var orderTextColor: Int
            when (lesson.order) {
                0 -> {
                    orderColor = R.color.lessonOrder1
                    orderTextColor = R.color.lessonOrder1Text
                }
                1 -> {
                    orderColor = R.color.lessonOrder2
                    orderTextColor = R.color.lessonOrder2Text
                }
                2 -> {
                    orderColor = R.color.lessonOrder3
                    orderTextColor = R.color.lessonOrder3Text
                }
                3 -> {
                    orderColor = R.color.lessonOrder4
                    orderTextColor = R.color.lessonOrder4Text
                }
                4 -> {
                    orderColor = R.color.lessonOrder5
                    orderTextColor = R.color.lessonOrder5Text
                }
                5 -> {
                    orderColor = R.color.lessonOrder6
                    orderTextColor = R.color.lessonOrder6Text
                }
                else -> {
                    orderColor = R.color.lessonOrder7
                    orderTextColor = R.color.lessonOrder7Text
                }
            }
            orderColor = viewBinding.textLessonOrder.context.getColor(orderColor)
            orderTextColor = viewBinding.textLessonOrder.context.getColor(orderTextColor)


            viewBinding.textviewLessonOrder.text = (lesson.order + 1).toString()
            viewBinding.textviewLessonOrder.setTextColor(orderTextColor)
            ImageViewCompat.setImageTintList(viewBinding.imageView2, ColorStateList.valueOf(orderColor))
        }

        private fun setTags(tags: List<Tag>) {
            viewBinding.tags.adapter = TagAdapter().apply { this.tags = tags }
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

        private fun setLessonTitleAndFeatures(lesson: Lesson, enabled: Boolean) {
            val builder = SpannableStringBuilder()
            val colorType = if (enabled) {
                (if (lesson.isImportant)
                    view.context.getColor(R.color.lessonTypeImportant)
                else
                    view.context.getColor(R.color.lessonTypeNotImportant))
            } else {
                disabledColor
            }
            val colorTextType = if (enabled) {
                (if (lesson.isImportant)
                    0xffFC3636.toInt()
                    //view.context.getColor(R.color.viewBinding.textScheduleFeaturesImportantText)
                else
                    0xff2574FF.toInt())
                    //view.context.getColor(R.color.viewBinding.textScheduleFeaturesNotImportantText))
            } else {
                0xffffffff.toInt()
            }

            val chipColor  = if (enabled) {
                chipColor
            } else {
                disabledColor
            }

            val chipTextColor  = if (enabled) {
                chipTextColor
            } else {
                0xffffffff.toInt()
            }

            val sp17 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                19f,
                view.resources.displayMetrics
            ).toInt()

            if (hasTime) {
                val (timeStart, timeEnd) = lesson.time
                viewBinding.textLessonTime.text = "$timeStart - $timeEnd"
//                builder.appendAny(
//                    "\u00A0",
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//                    RoundedBackgroundSpan(chipColor, height = sp17, text = "$timeStart - $timeEnd", textColor = chipTextColor),
//                    StyleSpan(Typeface.BOLD)
//                )
            }

            // Lesson type label
            if (hasTime) {
                //builder.append("  ")
            }
            builder.append(
                lesson.title
            )
//            builder.appendAny(
//                "\u00A0",
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//                RoundedBackgroundSpan(colorType, height = sp17, text = lesson.type, textColor = colorTextType),
//                StyleSpan(Typeface.BOLD)
//            )
            //builder.append(lesson.type, ForegroundColorSpan(colorType), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val dates: String
            if (lesson.dateFrom == lesson.dateTo) {
                dates = dateFormatter.format(lesson.dateFrom)
            } else {
                dates = dateFormatter.format(lesson.dateFrom) + " - " +
                        dateFormatter.format(lesson.dateTo)
            }

            // Duration label
            viewBinding.textLessonDuration.text = getDuration(lesson)
            viewBinding.textScheduleFeatures.text = lesson.type
            viewBinding.textScheduleDates.text = getDuration(lesson)//dates
            viewBinding.textScheduleFeatures.setTextColor(colorTextType)

            //lessonFeatures.text = builder
            viewBinding.textScheduleFeatures.isEnabled = enabled
            //lessonFeatures.visibility = View.GONE
            // Lesson title
            viewBinding.textScheduleTitle.text =  builder//lesson.title
        }

        private fun getDuration(lesson: Lesson): String {
            val expectedValueOfDaysInMonth = 30.44f

            val dayDuration = (lesson.dateFrom.until(lesson.dateTo, ChronoUnit.DAYS) + 1)

            val monthDuration = dayDuration / expectedValueOfDaysInMonth

            val monthDurationRounded: Float

            val floorDuration0 = floor(monthDuration)
            val floorDuration1 = floorDuration0 + 0.5f
            val floorDuration2 = floorDuration0 + 1f

            val decimalPlaces: Int
            monthDurationRounded = when {
                (monthDuration - floorDuration0) < abs(monthDuration - floorDuration1) -> {
                    decimalPlaces = 0
                    floorDuration0
                }
                abs(monthDuration - floorDuration1) < abs(floorDuration2 - monthDuration) -> {
                    decimalPlaces = 1
                    floorDuration1
                }
                else -> {
                    decimalPlaces = 0
                    floorDuration2
                }
            }

            return if (monthDurationRounded == 0f) {
                if (dayDuration == 1L) {
                    dateFormatter.format(lesson.dateFrom)
                } else {
                    "$dayDuration ${getEnding(dayDuration)}"
                }
            } else if (monthDurationRounded < 1f) {
                (dayDuration / 7f).roundToInt().toString() + " нед."
            } else {
                "%.${decimalPlaces}f мес.".format(monthDurationRounded)
            }
        }

        private fun getEnding(days: Long): String {
            // *1 день .. *2, *3, *4 дня .. *5, *6, *7, *8, *9, *0 дней .. искл. - 11 - 14
            val lastNumberOfDays = days % 10L
            return when {
                days in 11L..14L -> "дней"
                lastNumberOfDays == 1L -> "день"
                lastNumberOfDays in 2L..4L -> "дня"
                else -> "дней"
            }
        }

        private fun setAuditoriums(lesson: Lesson, enabled: Boolean) {
            viewBinding.textLessonAuditoriums.isEnabled = enabled
            if (lesson.auditoriums.isEmpty()) {
                viewBinding.textLessonAuditoriums.visibility = View.GONE
            } else {
                viewBinding.textLessonAuditoriums.visibility = View.VISIBLE
                viewBinding.textLessonAuditoriums.text = lesson.auditoriums.joinToString(separator = ", ") {
                    parseAuditoriumTitle(it.title)
                }
            }
        }

        private fun parseAuditoriumTitle(title: String): String {
            return SpannableString(
                HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            ).toString()
        }



        private fun setTeachers(lesson: Lesson, adapter: LessonAdapter, enabled: Boolean) {
            val teachers = if (lesson.teachers.size == 1)
                lesson.teachers.first().name
            else
                lesson.teachers.joinToString(", ") { it.getShortName() }

            if (!adapter.showTeachers || teachers.isEmpty()) {
                viewBinding.textLessonTeachers.visibility = View.GONE
            } else {
                viewBinding.textLessonTeachers.setText(teachers, TextView.BufferType.NORMAL)
                viewBinding.textLessonTeachers.isEnabled = enabled
                viewBinding.textLessonTeachers.visibility = View.VISIBLE
            }
        }

        private fun setGroups(lesson: Lesson, adapter: LessonAdapter, enabled: Boolean) {

            val groupsText = Group.getShort(lesson.groups)

            if (!adapter.showGroups || groupsText.isEmpty()) {
                viewBinding.textLessonGroups.visibility = View.GONE
            } else {
                viewBinding.textLessonGroups.setText(groupsText, TextView.BufferType.NORMAL)
                viewBinding.textLessonGroups.isEnabled = enabled
                viewBinding.textLessonGroups.visibility = View.VISIBLE
            }
        }

    }

    class ViewHolderEmpty(private val view: View): RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemLessonEmptyBinding::bind)

        fun bind(adapter: LessonAdapter) {
            val lesson = adapter.dailySchedule[adapterPosition]
            setBottomPadding()
            viewBinding.textLessonTime.text = "${lesson.time.first}, ${lesson.order + 1}-е занятие"
        }

        private fun setBottomPadding() {
            val dp8 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                view.resources.displayMetrics
            ).toInt()
            view.setPadding(
                view.paddingLeft,
                if (adapterPosition == 0) dp8 else 0,
                view.paddingRight,
                view.paddingBottom
            )
        }
    }

    class ViewHolderInfo(val view: View): RecyclerView.ViewHolder(view) {
        private lateinit var adapter: LessonAdapter
        private val viewBinding by viewBinding(ItemLessonInfoBinding::bind)

        fun bind(adapter: LessonAdapter) {
            this.adapter = adapter
            setInfo()
        }

        private fun setInfo() {
            val prevLesson = adapter.dailySchedule[adapterPosition - 1]
            val nextLesson = adapter.dailySchedule[adapterPosition + 1]

            val builderTitle = SpannableStringBuilder()
            var info: String
            val iconId: Int

            if (prevLesson.order == 2 && nextLesson.order - prevLesson.order == 1) {
                builderTitle.append("Большой перерыв")
//                builderTitle.appendAny(", ${prevLesson.time.second} - ${nextLesson.time.first}",
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//                    RelativeSizeSpan(0.8f),
//                    ForegroundColorSpan(view.context.getColor(R.color.textSecondary)))

                info = "У тебя будет 40 минут, чтобы перекусить или отдохнуть"
                iconId = R.drawable.ic_round_local_cafe_24
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
                builderTitle.append("Окно между занятиями")
//                builderTitle.appendAny(", ${prevLesson.time.second} - ${nextLesson.time.first}",
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//                    RelativeSizeSpan(0.8f),
//                    ForegroundColorSpan(view.context.getColor(R.color.textSecondary)))
                info = "У тебя будет $windowTimeHours час$endingHours"
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
                info += ", чтобы перекусить или отдохнуть"
                iconId = when {
                    totalMinutes < 180 -> R.drawable.ic_baseline_fastfood_24
                    totalMinutes < 270 -> R.drawable.ic_round_sports_esports_24
                    else -> R.drawable.ic_round_sports_volleyball_24
                }
            }
            viewBinding.imageviewInfo.setImageResource(iconId)
            viewBinding.textTitle.text = builderTitle
            viewBinding.textInfo.text = info
        }
    }
}

fun getTime(totalMinutes: Long, isGenitive: Boolean): String {
    val timeLeft = StringBuilder()
    val windowTimeHours = totalMinutes / 60L
    val windowTimeMinutes = totalMinutes % 60
    if (windowTimeHours != 0L) {
        timeLeft.append("$windowTimeHours ч.")
    }
    if (windowTimeMinutes != 0L) {
        if (windowTimeHours != 0L) {
            timeLeft.append(" ")
        }
        timeLeft.append("$windowTimeMinutes мин.")
    }

    if (windowTimeHours == 0L && windowTimeMinutes == 0L) {
        timeLeft.append("менее минуты")
    }

    return timeLeft.toString()
}

private fun SpannableStringBuilder.appendAny(text: String, flags: Int, vararg spans: Any) {
    val start = length
    append(text)
    val length = length
    for (span in spans) {
        setSpan(span, start, length, flags)
    }
}