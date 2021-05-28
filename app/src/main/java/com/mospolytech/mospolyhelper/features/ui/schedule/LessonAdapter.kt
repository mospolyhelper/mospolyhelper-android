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
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemLessonBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonInfoBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonTimeBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag.getColor
import com.mospolytech.mospolyhelper.features.ui.schedule.model.DailySchedulePack
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonPack
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonPlacePack
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonWindowPack
import com.mospolytech.mospolyhelper.utils.RoundedBackgroundSpan
import com.mospolytech.mospolyhelper.utils.dp
import com.mospolytech.mospolyhelper.utils.setSafeOnClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

class LessonAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_LESSON = 0
        const val VIEW_TYPE_LESSON_EMPTY = 1
        const val VIEW_TYPE_INFO = 2
        const val VIEW_TYPE_TIME = 3

        private val dateFormatter = DateTimeFormatter.ofPattern("d MMM")
    }

    var dailySchedule: DailySchedulePack = DailySchedulePack(
        emptyList(),
        LocalDate.now(),
        LessonFeaturesSettings(
            showGroups = false,
            showTeachers = false,
            showAuditoriums = false
        )
    )


    var lessonClick: (LessonTime, Lesson, LocalDate) -> Unit = { _, _, _ -> }

    override fun getItemCount() = dailySchedule.lessons.size

    fun submitList(dailySchedule: DailySchedulePack) {
        this.dailySchedule = dailySchedule
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (val scheduleItem = dailySchedule.lessons[position]) {
            is LessonWindowPack -> VIEW_TYPE_INFO
            is LessonPlacePack -> VIEW_TYPE_TIME
            is LessonPack ->
                if (scheduleItem.lesson.isEmpty) VIEW_TYPE_LESSON_EMPTY else VIEW_TYPE_LESSON
            else -> VIEW_TYPE_LESSON_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TIME -> ViewHolderTime(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lesson_time, parent, false)
            )
            VIEW_TYPE_LESSON -> ViewHolderLesson(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lesson, parent, false)
            )
            VIEW_TYPE_LESSON_EMPTY -> ViewHolderEmptyLesson(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lesson_empty, parent, false)
            )
            VIEW_TYPE_INFO -> ViewHolderInfo(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lesson_info, parent, false)
            )
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderTime ->
                holder.bind(dailySchedule.lessons[position] as LessonPlacePack)
            is ViewHolderLesson ->
                holder.bind(
                    dailySchedule.lessons[position] as LessonPack,
                    dailySchedule.date,
                    itemCount - 1
                ) { time, lesson ->
                    lessonClick.invoke(time, lesson, dailySchedule.date)
                }
            is ViewHolderInfo ->
                holder.bind(dailySchedule.lessons[position] as LessonWindowPack)
        }
    }

    class ViewHolderLesson(
        val view: View
    ) : RecyclerView.ViewHolder(view) {
        private val disabledColor = view.context.getColor(R.color.textSecondaryDisabled)
        private val viewBinding by viewBinding(ItemLessonBinding::bind)

        fun bind(
            lessonPack: LessonPack,
            date: LocalDate,
            lastItemPosition: Int,
            onLessonClick: (LessonTime, Lesson) -> Unit
        ) {
            viewBinding.layoutLesson.setSafeOnClickListener {
                onLessonClick(lessonPack.lessonTime, lessonPack.lesson)
            }
            val enabled = date in lessonPack.lesson.dateFrom..lessonPack.lesson.dateTo

            setBottomPadding(lastItemPosition)
            setLessonType(lessonPack.lesson, enabled)
            setLessonDuration(lessonPack.lesson)
            setLessonTitle(lessonPack.lesson, enabled)
            setAuditoriums(lessonPack.lesson, lessonPack.featuresSettings, enabled)
            setTeachers(lessonPack.lesson, lessonPack.featuresSettings, enabled)
            setGroups(lessonPack.lesson, lessonPack.featuresSettings, enabled)
            setTags(lessonPack.tags)
        }

        private fun setBottomPadding(lastItemPosition: Int) {
            var paddingBottom = 0
            if (bindingAdapterPosition == lastItemPosition) {
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
            val dp3 = 3.dp(itemView.context).toInt()
            view.setPadding(view.paddingLeft, dp3, view.paddingRight, paddingBottom)
        }

        private fun setTags(tags: List<LessonTag>) {
            if (tags.isEmpty()) {
                viewBinding.tags.visibility = View.GONE
            } else {
                viewBinding.tags.visibility = View.VISIBLE
                viewBinding.tags.text = getFeaturesString(tags)
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

        private fun getFeaturesString(features: List<LessonTag>): SpannableStringBuilder {
            val iterator = features.iterator()
            val builder = SpannableStringBuilder()
            while (iterator.hasNext()) {
                val feature = iterator.next()
                val color = feature.getColor()
                builder.append(
                    "\u00A0",
                    RoundedBackgroundSpan(
                        backgroundColor = itemView.context.getColor(color.colorId),
                        textColor = itemView.context.getColor(color.textColorId),
                        text = feature.title,
                        relativeTextSize = 0.65f
                    ),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (iterator.hasNext()) {
                    builder.append(" ")
                }
            }
            return builder
        }

        private fun setLessonType(lesson: Lesson, enabled: Boolean) {
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

            viewBinding.textviewLessonType.text = lesson.type
            viewBinding.textviewLessonType.setTextColor(colorTextType)
            viewBinding.textviewLessonType.isEnabled = enabled
        }

        private fun setLessonDuration(lesson: Lesson) {
            viewBinding.textScheduleDates.text = getDuration(lesson)
        }

        private fun setLessonTitle(lesson: Lesson, enabled: Boolean) {
            viewBinding.textviewLessonTitle.text = lesson.title
            viewBinding.textviewLessonType.isEnabled = enabled
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

        private fun setAuditoriums(
            lesson: Lesson,
            featuresSettings: LessonFeaturesSettings,
            enabled: Boolean
        ) {
            viewBinding.textLessonAuditoriums.isEnabled = enabled
            if (!featuresSettings.showAuditoriums || lesson.auditoriums.isEmpty()) {
                viewBinding.textLessonAuditoriums.visibility = View.GONE
            } else {
                viewBinding.textLessonAuditoriums.visibility = View.VISIBLE
                viewBinding.textLessonAuditoriums.text =
                    lesson.auditoriums.joinToString(separator = ", ") {
                        parseAuditoriumTitle(it.title)
                    }
            }
        }

        private fun parseAuditoriumTitle(title: String): String {
            return SpannableString(
                HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            ).toString()
        }


        private fun setTeachers(
            lesson: Lesson,
            featuresSettings: LessonFeaturesSettings,
            enabled: Boolean
        ) {
            val teachers = if (lesson.teachers.size == 1)
                lesson.teachers.first().name
            else
                lesson.teachers.joinToString(", ") { it.getShortName() }

            if (!featuresSettings.showTeachers || teachers.isEmpty()) {
                viewBinding.textLessonTeachers.visibility = View.GONE
            } else {
                viewBinding.textLessonTeachers.setText(teachers, TextView.BufferType.NORMAL)
                viewBinding.textLessonTeachers.isEnabled = enabled
                viewBinding.textLessonTeachers.visibility = View.VISIBLE
            }
        }

        private fun setGroups(
            lesson: Lesson,
            featuresSettings: LessonFeaturesSettings,
            enabled: Boolean
        ) {

            val groupsText = Group.getShort(lesson.groups)

            if (!featuresSettings.showGroups || groupsText.isEmpty()) {
                viewBinding.textLessonGroups.visibility = View.GONE
            } else {
                viewBinding.textLessonGroups.setText(groupsText, TextView.BufferType.NORMAL)
                viewBinding.textLessonGroups.isEnabled = enabled
                viewBinding.textLessonGroups.visibility = View.VISIBLE
            }
        }

    }

    class ViewHolderTime(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemLessonTimeBinding::bind)

        fun bind(lessonPlacePack: LessonPlacePack) {
            setTime(lessonPlacePack.lessonPlace)
        }

        private fun setTime(lessonPlace: LessonPlace) {
            val orderColor = when (lessonPlace.order) {
                0 -> R.color.lessonOrder1
                1 -> R.color.lessonOrder2
                2 -> R.color.lessonOrder3
                3 -> R.color.lessonOrder4
                4 -> R.color.lessonOrder5
                5 -> R.color.lessonOrder6
                else -> R.color.lessonOrder7
            }

            TextViewCompat.setCompoundDrawableTintList(
                viewBinding.textLessonTime,
                ColorStateList.valueOf(itemView.context.getColor(orderColor))
            )
            val (timeStart, timeEnd) = lessonPlace.time
            viewBinding.textLessonTime.text =
                "$timeStart - $timeEnd" //+ ", ${lessonPlace.order + 1}-я пара"
        }
    }

    class ViewHolderEmptyLesson(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolderInfo(val view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemLessonInfoBinding::bind)

        fun bind(lessonWindow: LessonWindowPack) {
            setInfo(
                lessonWindow.lessonWindow.previousLessonPlace,
                lessonWindow.lessonWindow.nextLessonPlace
            )
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