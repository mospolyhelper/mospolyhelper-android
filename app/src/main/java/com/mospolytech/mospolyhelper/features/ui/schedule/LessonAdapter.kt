package com.mospolytech.mospolyhelper.features.ui.schedule

import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemLessonBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonInfoBinding
import com.mospolytech.mospolyhelper.databinding.ItemLessonTimeBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.group.Group
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.utils.fullTitle
import com.mospolytech.mospolyhelper.domain.schedule.utils.isOnline
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag.getColor
import com.mospolytech.mospolyhelper.features.ui.schedule.model.*
import com.mospolytech.mospolyhelper.features.utils.RoundedBackgroundSpan
import com.mospolytech.mospolyhelper.features.utils.getColor
import com.mospolytech.mospolyhelper.features.utils.getColorStateList
import com.mospolytech.mospolyhelper.utils.WeakMutableSet
import com.mospolytech.mospolyhelper.utils.setSafeOnClickListener
import java.time.LocalDate
import java.time.LocalTime
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
    private var currentTimes: List<LessonTime> = emptyList()
    private val activeViewHolders: MutableSet<RecyclerView.ViewHolder?> = WeakMutableSet()


    var lessonClick: (LessonTime, Lesson, LocalDate) -> Unit = { _, _, _ -> }

    override fun getItemCount() = dailySchedule.lessons.size

    fun submitList(dailySchedule: DailySchedulePack, currentTimes: List<LessonTime>) {
        this.dailySchedule = dailySchedule
        this.currentTimes = currentTimes
        notifyDataSetChanged()
    }

    fun setCurrentLessonTimes(currentTimes: List<LessonTime>) {
        this.currentTimes = currentTimes
        for (holder in activeViewHolders) {
            if (holder is ViewHolderTime) {
                val lessonTimePack = dailySchedule.lessons[holder.bindingAdapterPosition] as LessonTimePack
                holder.setTime(lessonTimePack, lessonTimePack.time in currentTimes)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val scheduleItem = dailySchedule.lessons[position]) {
            is LessonWindowPack -> VIEW_TYPE_INFO
            is LessonTimePack -> VIEW_TYPE_TIME
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
        activeViewHolders.add(holder)
        when (holder) {
            is ViewHolderTime -> {
                val lessonTimePack = dailySchedule.lessons[position] as LessonTimePack
                holder.bind(lessonTimePack, lessonTimePack.time in currentTimes)
            }
            is ViewHolderLesson ->
                holder.bind(
                    dailySchedule.lessons[position] as LessonPack
                ) { time, lesson ->
                    lessonClick.invoke(time, lesson, dailySchedule.date)
                }
            is ViewHolderInfo ->
                holder.bind(dailySchedule.lessons[position] as LessonWindowPack)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        activeViewHolders.remove(holder)
    }

    class ViewHolderLesson(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemLessonBinding::bind)

        fun bind(
            lessonPack: LessonPack,
            onLessonClick: (LessonTime, Lesson) -> Unit
        ) {
            viewBinding.layoutLesson.setSafeOnClickListener {
                onLessonClick(lessonPack.lessonTime, lessonPack.lesson)
            }
            setLessonType(lessonPack.lesson, lessonPack.isEnabled)
            setLessonDuration(lessonPack.lesson, lessonPack.isEnabled)
            setLessonTitle(lessonPack.lesson, lessonPack.isEnabled)
            setAuditoriums(lessonPack.lesson, lessonPack.featuresSettings, lessonPack.isEnabled)
            setTeachers(lessonPack.lesson, lessonPack.featuresSettings, lessonPack.isEnabled)
            setGroups(lessonPack.lesson, lessonPack.featuresSettings, lessonPack.isEnabled)
            setTags(lessonPack.tags)
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

        private fun getFeaturesString(tags: List<LessonTag>): SpannableStringBuilder {
            val iterator = tags.iterator()
            val builder = SpannableStringBuilder()
            while (iterator.hasNext()) {
                val tag = iterator.next()
                val color = tag.getColor()
                builder.append(
                    "\u00A0",
                    RoundedBackgroundSpan(
                        backgroundColor = itemView.context.getColor(color.colorId),
                        textColor = itemView.context.getColor(color.textColorId),
                        text = tag.title,
                        relativeTextSize = 0.65f
                    ),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (iterator.hasNext()) {
                    builder.append(" ")
                } else {
                    builder.append(" ")
                }
            }
            return builder
        }

        private fun setLessonType(lesson: Lesson, enabled: Boolean) {
            if (enabled && lesson.isImportant) {
                viewBinding.textviewLessonType.setTextColor(
                    getColor(R.color.text_color_lesson_type_important)
                )
            } else {
                viewBinding.textviewLessonType.setTextColor(
                    getColorStateList(R.color.text_color_tertiary)
                )
            }


            viewBinding.textviewLessonType.text = lesson.type

            viewBinding.textviewLessonType.isEnabled = enabled
        }

        private fun setLessonDuration(lesson: Lesson, enabled: Boolean) {
            viewBinding.textScheduleDates.text = getDuration(lesson)
            viewBinding.textScheduleDates.isEnabled = enabled
        }

        private fun setLessonTitle(lesson: Lesson, enabled: Boolean) {
            viewBinding.textviewLessonTitle.text = lesson.title
            viewBinding.textviewLessonTitle.isEnabled = enabled
        }

        private fun getDuration(lesson: Lesson): String {
            val expectedValueOfDaysInMonth = 30.44f

            val days = (lesson.dateFrom.until(lesson.dateTo, ChronoUnit.DAYS) + 1)
            val months = days / expectedValueOfDaysInMonth

            val floorDuration0 = floor(months)
            val floorDuration1 = floorDuration0 + 0.5f
            val floorDuration2 = floorDuration0 + 1f

            val decimalsAfterDot: Int
            // Reason of this - it's supposed that for student it's better to
            // overestimate own time than underestimate own one.
            // That why we round down months in such way
            val monthDurationRounded = when {
                // x.0 until x.5 become x
                (months - floorDuration0) < abs(months - floorDuration1) -> {
                    decimalsAfterDot = 0
                    floorDuration0
                }
                // x.5 until x + 1 become x.5
                abs(months - floorDuration1) < abs(floorDuration2 - months) -> {
                    decimalsAfterDot = 1
                    floorDuration1
                }
                // else become x.5
                else -> {
                    decimalsAfterDot = 0
                    floorDuration2
                }
            }

            return if (monthDurationRounded == 0f) {
                if (days == 1L) {
                    lesson.dateFrom.format(dateFormatter)
                } else {
                    "$days ${getDaysText(days)}"
                }
            } else if (monthDurationRounded < 1f) {
                (days / 7f).roundToInt().toString() + " нед."
            } else {
                "%.${decimalsAfterDot}f мес.".format(monthDurationRounded)
            }
        }

        private fun getDaysText(days: Long): String {
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
                val iconRes = if (lesson.auditoriums.any { it.isOnline }) {
                    R.drawable.ic_fluent_desktop_20_regular
                } else {
                    R.drawable.ic_fluent_location_20_regular
                }
                viewBinding.textLessonAuditoriums
                    .setCompoundDrawablesRelativeWithIntrinsicBounds(
                        iconRes, 0, 0, 0
                    )
                viewBinding.textLessonAuditoriums.text =
                    lesson.auditoriums.joinToString(separator = ", ") {
                        it.fullTitle
                    }
                viewBinding.textLessonAuditoriums.visibility = View.VISIBLE
            }
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

        fun bind(lessonTimePack: LessonTimePack, isCurrent: Boolean) {
            setTime(lessonTimePack, isCurrent)
        }

        fun setTime(lessonTimePack: LessonTimePack, isCurrent: Boolean) {
            if (isCurrent) {
                setCurrentTime(lessonTimePack.time)
            } else {
                setLessonTime(lessonTimePack.time)
            }
        }

        private fun setCurrentTime(time: LessonTime) {
            val (timeStart, timeEnd) = time.timeString
            val totalMinutes = LocalTime.now().until(
                time.localTime.start, ChronoUnit.MINUTES
            )
            val formattedTime = getTimeText(abs(totalMinutes))
            val resultTime = when {
                totalMinutes > 0L -> itemView.context.getString(
                    R.string.schedule_lesson_time_not_started, timeStart, formattedTime
                )
                totalMinutes == 0L -> itemView.context.getString(
                    R.string.schedule_lesson_time_almost_started, formattedTime
                )
                else -> itemView.context.getString(
                    R.string.schedule_lesson_time_current, formattedTime, timeEnd
                )
            }
            viewBinding.textLessonTime.text = resultTime
            viewBinding.textLessonTime.setTextColor(0xffFF7200.toInt())
            if (totalMinutes > 0L) {
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    viewBinding.textLessonTime,
                    R.drawable.ic_fluent_alert_20_regular, 0, 0, 0
                )
            } else {
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    viewBinding.textLessonTime,
                    R.drawable.ic_fluent_alert_20_filled, 0, 0, 0
                )
            }
            TextViewCompat.setCompoundDrawableTintList(
                viewBinding.textLessonTime,
                ColorStateList.valueOf(0xffFF7200.toInt())
            )
        }

        private fun setLessonTime(time: LessonTime) {
            val orderColor = when (time.order) {
                0 -> R.color.lessonOrder1
                1 -> R.color.lessonOrder2
                2 -> R.color.lessonOrder3
                3 -> R.color.lessonOrder4
                4 -> R.color.lessonOrder5
                5 -> R.color.lessonOrder6
                else -> R.color.lessonOrder7
            }
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                viewBinding.textLessonTime,
                R.drawable.ring, 0, 0, 0
            )
            TextViewCompat.setCompoundDrawableTintList(
                viewBinding.textLessonTime,
                ColorStateList.valueOf(itemView.context.getColor(orderColor))
            )
            val (timeStart, timeEnd) = time.timeString
            viewBinding.textLessonTime.text = itemView.context.getString(
                R.string.schedule_lesson_time,
                timeStart,
                timeEnd
            )
            viewBinding.textLessonTime.setTextColor(getColor(R.color.text_color_secondary))
        }

        private fun getTimeText(totalMinutes: Long): String {
            val resTime = StringBuilder()
            val windowTimeHours = totalMinutes / 60L
            val windowTimeMinutes = totalMinutes % 60
            if (windowTimeHours != 0L) {
                resTime.append("$windowTimeHours ч.")
            }
            if (windowTimeMinutes != 0L) {
                if (windowTimeHours != 0L) {
                    resTime.append(" ")
                }
                resTime.append("$windowTimeMinutes мин.")
            }

            if (totalMinutes == 0L) {
                resTime.append("менее минуты")
            }

            return resTime.toString()
        }
    }

    class ViewHolderEmptyLesson(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolderInfo(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemLessonInfoBinding::bind)

        fun bind(lessonWindow: LessonWindowPack) {
            setInfo(
                lessonWindow.lessonWindow.previousLessonTime,
                lessonWindow.lessonWindow.nextLessonTime
            )
        }

        private fun setInfo(
            prevLesson: LessonTime,
            nextLesson: LessonTime
        ) {
            val title: String
            val info: String
            val iconId: Int

            if (prevLesson.order == 2 && nextLesson.order - prevLesson.order == 1) {
                title = itemView.context.getString(R.string.schedule_info_break_title)
                info = itemView.context.getString(R.string.schedule_info_break_description)
                iconId = R.drawable.ic_fluent_drink_coffee_20_regular
            } else {
                val totalMinutes = prevLesson.localTime.end.until(
                    nextLesson.localTime.start, ChronoUnit.MINUTES
                )
                title = itemView.context.getString(R.string.schedule_info_window_title)
                info = itemView.context.getString(
                    R.string.schedule_info_window_description,
                    getTimeText(totalMinutes)
                )
                iconId = when {
                    totalMinutes < 180 -> R.drawable.ic_fluent_food_pizza_20_regular
                    totalMinutes < 270 -> R.drawable.ic_fluent2_games_20_regular
                    else -> R.drawable.ic_fluent_phone_desktop_20_regular
                }
            }
            viewBinding.textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(iconId, 0, 0, 0)
            viewBinding.textTitle.text = title
            viewBinding.textInfo.text = info
        }

        private fun getTimeText(totalMinutes: Long): String {
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
            var resTime = "$windowTimeHours час$endingHours"
            if (windowTimeMinutes != 0L) {
                // *1 минута .. *2, *3, *4 минуты .. *5, *6, *7, *8, *9, *0 минут .. искл. - 11 - 14
                val lastNumberOfMinutes = windowTimeMinutes % 10
                val endingMinutes = when {
                    windowTimeMinutes in 11L..14L -> ""
                    lastNumberOfMinutes == 1L -> "а"
                    lastNumberOfMinutes in 2L..4L -> "ы"
                    else -> ""
                }
                resTime += " $windowTimeMinutes минут$endingMinutes"
            }

            return resTime
        }
    }
}