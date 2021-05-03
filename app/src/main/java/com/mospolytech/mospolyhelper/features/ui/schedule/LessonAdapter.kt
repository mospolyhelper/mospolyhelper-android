package com.mospolytech.mospolyhelper.features.ui.schedule

import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemLessonBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonEmptyBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonInfoBinding
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.utils.Action2
import com.mospolytech.mospolyhelper.utils.Event2
import com.mospolytech.mospolyhelper.utils.RoundedBackgroundSpan
import com.mospolytech.mospolyhelper.utils.setSafeOnClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

class LessonAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_LESSON_PLACE = 0
        const val VIEW_TYPE_LESSON_PLACE_EMPTY = 1
        const val VIEW_TYPE_INFO = 2

        private val dateFormatter = DateTimeFormatter.ofPattern("d MMM")
    }

    var dailySchedule: List<ScheduleItem> = emptyList()
    val tags: Map<LessonTagKey, List<LessonTag>> = emptyMap()
    val deadlines: Map<String, List<Deadline>> = emptyMap()
    var date: LocalDate = LocalDate.now()
    var showGroups: Boolean = false
    var showTeachers: Boolean = false

    val lessonClick: Event2<LessonPlace, LocalDate> = Action2()

    override fun getItemCount() = dailySchedule.size

    private var currentOrder = -2
    private var currentOrderIsStarted = false
    private var cleared = true

    private var currentOrderEvening = -2
    private var currentOrderIsStartedEvening = false
    private var clearedEvening = true


    fun submitList(
        dailySchedule: List<ScheduleItem>,
        date: LocalDate,
        showGroups: Boolean,
        showTeachers: Boolean
    ) {
        this.dailySchedule = dailySchedule
        this.showGroups = showGroups
        this.showTeachers = showTeachers
        this.date = date
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val scheduleItem = dailySchedule[position]
        return when (scheduleItem) {
            is LessonWindow -> VIEW_TYPE_INFO
            is LessonPlace ->
                if (scheduleItem.lessons.isEmpty()) VIEW_TYPE_LESSON_PLACE_EMPTY else VIEW_TYPE_LESSON_PLACE
            else -> VIEW_TYPE_LESSON_PLACE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LESSON_PLACE -> {
                val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_lesson, parent, false)
                ViewHolder(view, viewType)
            }
            VIEW_TYPE_LESSON_PLACE_EMPTY -> {
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
            else -> onCreateViewHolder(parent, VIEW_TYPE_LESSON_PLACE)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            VIEW_TYPE_LESSON_PLACE -> {
                val lessonPlace = dailySchedule[position] as LessonPlace
                val tags = emptyList<LessonTag>()//tags[LessonTagKey.fromLesson(lesson)] ?: emptyList()
                (viewHolder as ViewHolder).bind(this, lessonPlace, tags) { lesson, date ->
                    (lessonClick as Action2).invoke(lesson, date) }
            }
            VIEW_TYPE_LESSON_PLACE_EMPTY -> {
                val lessonPlace = dailySchedule[position] as LessonPlace
                (viewHolder as ViewHolderEmpty).bind(lessonPlace, this)
            }
            VIEW_TYPE_INFO -> {
                val lessonWindows = dailySchedule[position] as LessonWindow
                val prevLesson = if (position - 1 >= 0) dailySchedule[position - 1] as LessonPlace else LessonPlace(
                    emptyList(), 0, false)
                val nextLesson = if (position + 1 < dailySchedule.size) dailySchedule[position + 1] as LessonPlace else LessonPlace(
                        emptyList(), 6, false)
                (viewHolder as ViewHolderInfo).bind(lessonWindows, prevLesson, nextLesson, this)
            }
        }
    }

    class ViewHolder(
        val view: View,
        preViewType: Int
    ) : RecyclerView.ViewHolder(view) {
        private val disabledColor = view.context.getColor(R.color.textSecondaryDisabled)
        private val viewBinding by viewBinding(ItemLessonBinding::bind)

        init {
            viewBinding.tags.layoutManager = FlexboxLayoutManager(view.context).apply {
                recycleChildrenOnDetach = true
            }
        }

        // ViewHolder class is not inner because recycler views have common pool
        fun bind(
            adapter: LessonAdapter,
            lessonPlace: LessonPlace,
            tags: List<LessonTag>,
            onLessonClick: (LessonPlace, LocalDate) -> Unit
        ) {
            val lesson = lessonPlace.lessons.firstOrNull() ?: Lesson.getEmpty()
            viewBinding.layoutLesson.setSafeOnClickListener {
                onLessonClick(lessonPlace.copy(lessons = listOf(lesson)), adapter.date)
            }
            val enabled = adapter.date in lesson.dateFrom..lesson.dateTo

            setBottomPadding(adapter)
            setTime(lessonPlace, adapter)
            setLessonTitleAndFeatures(lesson, lessonPlace, enabled)
            setAuditoriums(lesson, enabled)
            setTeachers(lesson, adapter, enabled)
            setGroups(lesson, adapter, enabled)
            setTags(tags)
            setAnotherLessons(lessonPlace)
        }

        private fun setAnotherLessons(lessonPlace: LessonPlace) {
            val hide = lessonPlace.lessons.size == 1

            if (hide) {
                viewBinding.viewLessonsSeparator.visibility = View.GONE
                viewBinding.textviewAnotherLessons.visibility = View.GONE
            } else {
                viewBinding.textviewAnotherLessons.text = "Ещё ${lessonPlace.lessons.size - 1} занятие в это время"
                viewBinding.viewLessonsSeparator.visibility = View.VISIBLE
                viewBinding.textviewAnotherLessons.visibility = View.VISIBLE
            }
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

        private fun setTime(lessonPlace: LessonPlace, adapter: LessonAdapter) {

            val currentOrder: Int
            val currentLessonIsStarted: Boolean
            if (lessonPlace.isEvening) {
                currentOrder = adapter.currentOrderEvening
                currentLessonIsStarted = adapter.currentOrderIsStartedEvening
            } else {
                currentOrder = adapter.currentOrder
                currentLessonIsStarted = adapter.currentOrderIsStarted
            }

            // Current lessons label
//            if (order == currentOrder) {
//                val currentLessonText: String
//                if (currentLessonIsStarted) {
//                    val time = getTime((lesson.localTime.first.until(LocalTime.now(), ChronoUnit.SECONDS) / 60f).roundToLong(), true)
//                    currentLessonText = "Идёт ${time.toLowerCase()}"
//                } else {
//                    val time = getTime((LocalTime.now().until(lesson.localTime.first, ChronoUnit.SECONDS) / 60f).roundToLong(), false)
//                    currentLessonText = "До начала ${time.toLowerCase()}"
//                }
//
//                viewBinding.textLessonCurrent.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_current_lesson, 0, 0, 0)
//                viewBinding.textLessonCurrent.text = currentLessonText
//                viewBinding.textLessonCurrent.visibility = View.VISIBLE
//            } else {
                //viewBinding.textLessonCurrent.visibility = View.GONE
            //}

            var orderColor: Int
            var orderTextColor: Int
            when (lessonPlace.order) {
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
            orderColor = viewBinding.root.context.getColor(orderColor)


            viewBinding.textLessonTime.compoundDrawableTintList = ColorStateList.valueOf(orderColor)

            val (timeStart, timeEnd) = lessonPlace.time
            viewBinding.textLessonTime.text = "$timeStart - $timeEnd" //+ ", ${lessonPlace.order + 1}-я пара"
        }

        private fun setTags(tags: List<LessonTag>) {
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

        private fun getFeaturesString(features: Iterable<String>, isImportant: Boolean): SpannableStringBuilder {
            var oneFlag = true
            val iterator = features.iterator()
            val builder = SpannableStringBuilder()
            while (iterator.hasNext()) {
                val backgroundColor: Int
                val textColor: Int
                if (oneFlag) {
                    oneFlag = false
                    if (isImportant) {
                        backgroundColor = view.context.getColor(R.color.featureBackgroundImportant)
                        textColor = view.context.getColor(R.color.featureTextImportant)
                    } else {
                        backgroundColor = view.context.getColor(R.color.featureBackgroundNotImportant)
                        textColor = view.context.getColor(R.color.featureTextNotImportant)
                    }

                } else {
                    backgroundColor = view.context.getColor(R.color.featureBackground)
                    textColor = view.context.getColor(R.color.featureText)
                }

                val feature = iterator.next()
                builder.append(
                    "\u00A0",
                    RoundedBackgroundSpan(
                        backgroundColor = backgroundColor,
                        textColor = textColor,
                        text = feature
                    ),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (iterator.hasNext()) {
                    builder.append(" ")
                }
            }
            return builder
        }

        private fun setLessonTitleAndFeatures(lesson: Lesson, lessonPlace: LessonPlace, enabled: Boolean) {
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
                    0xff818289.toInt())
                    //view.context.getColor(R.color.viewBinding.textScheduleFeaturesNotImportantText))
            } else {
                0xffffffff.toInt()
            }


            // Lesson type label
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
//            viewBinding.textScheduleFeatures.text = getFeaturesString(
//                listOf(
//                    lesson.type,
//                    "${lessonPlace.order + 1}-я пара"
//
//                ),
//                lesson.isImportant
//            )
            // Todo: add support for 3rd pair
            viewBinding.textScheduleDates.text = getDuration(lesson)//dates
            viewBinding.textScheduleFeatures.setTextColor(colorTextType)

            //lessonFeatures.text = builder
            viewBinding.textScheduleFeatures.isEnabled = enabled
            //lessonFeatures.visibility = View.GONE
            // Lesson title
            viewBinding.textScheduleTitle.text =  builder
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

        fun bind(lessonPlace: LessonPlace, adapter: LessonAdapter) {
            setBottomPadding()
            viewBinding.textLessonTime.text = "${lessonPlace.time.first}, ${lessonPlace.order + 1}-е занятие"
        }

        private fun setBottomPadding() {
            val dp8 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                view.resources.displayMetrics
            ).toInt()
            view.setPadding(
                view.paddingLeft,
                if (bindingAdapterPosition == 0) dp8 else 0,
                view.paddingRight,
                view.paddingBottom
            )
        }
    }

    class ViewHolderInfo(val view: View): RecyclerView.ViewHolder(view) {
        private lateinit var adapter: LessonAdapter
        private val viewBinding by viewBinding(ItemLessonInfoBinding::bind)

        fun bind(
            lessonWindow: LessonWindow,
            prevLesson: LessonPlace,
            nextLesson: LessonPlace,
            adapter: LessonAdapter
        ) {
            this.adapter = adapter
            setInfo(prevLesson, nextLesson)
        }

        private fun setInfo(
            prevLesson: LessonPlace,
            nextLesson: LessonPlace
        ) {
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
                iconId = R.drawable.ic_fluent_drink_coffee_20_regular
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
                    totalMinutes < 180 -> R.drawable.ic_fluent_food_pizza_20_regular
                    totalMinutes < 270 -> R.drawable.ic_fluent_games_20_regular
                    else -> R.drawable.ic_fluent_book_20_regular
                }
            }
            //viewBinding.imageviewInfo.setImageResource(iconId)
            viewBinding.textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(iconId, 0, 0, 0)
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