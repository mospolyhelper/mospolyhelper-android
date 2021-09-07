package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ScheduleUiData(
    private val schedule: Schedule,
    private val tags: List<LessonTag>,
    private val deadlines: Map<String, List<Deadline>>,
    private val settings: ScheduleSettings,
    private val dates: ClosedRange<LocalDate>
) : List<DailySchedulePack> {

    companion object {
        private const val MAX_COUNT = 400
    }

    override val size: Int = dates.start.until(dates.endInclusive, ChronoUnit.DAYS).toInt()

    private val cachedDays = MutableList<DailySchedulePack?>(size) { null }

    private fun getDateByPosition(position: Int): LocalDate {
        return dates.start.plusDays(position.toLong())
    }

    override fun isEmpty() = size == 0

    override fun get(index: Int): DailySchedulePack {
        val cachedDay = cachedDays[index]
        if (cachedDay == null) {
            val dailySchedule = DailySchedulePack.Builder()
                .withEmptyLessons(settings.showEmptyLessons)
                .withLessonWindows(true)
                .build(
                    schedule,
                    getDateByPosition(index),
                    settings.dateFilter,
                    settings.lessonFeatures,
                    { lesson, dayOfWeek, order ->
                        val tagKey = LessonTagKey.fromLesson(lesson, dayOfWeek, order)
                        tags.filter { it.lessons.contains(tagKey) }
                    },
                    { lesson ->
                        deadlines[lesson.title] ?: emptyList()
                    }
                )
            cachedDays[index] = dailySchedule

            return dailySchedule
        }
         else {
             return cachedDay
        }
    }


    override fun indexOf(element: DailySchedulePack): Int {
        for ((index, item) in this.withIndex()) {
            if (element == item)
                return index
        }
        return -1
    }

    override fun contains(element: DailySchedulePack): Boolean =
        this.indexOf(element) != -1

    override fun containsAll(elements: Collection<DailySchedulePack>): Boolean =
        this.all { contains(it) }

    override fun iterator(): Iterator<DailySchedulePack> =
        ScheduleListIterator()

    override fun lastIndexOf(element: DailySchedulePack): Int {
        for (i in size - 1 downTo 0) {
            if (element == get(i))
                return i
        }
        return -1
    }

    override fun listIterator(): ListIterator<DailySchedulePack> =
        ScheduleListIterator()

    override fun listIterator(index: Int): ListIterator<DailySchedulePack> =
        ScheduleListIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<DailySchedulePack> {
        return MutableList(toIndex - fromIndex) { get(fromIndex + it) }
    }

    private inner class ScheduleListIterator(
        private val startIndex: Int = 0
        ) : ListIterator<DailySchedulePack> {
        private var currentIndex = startIndex - 1

        override fun hasNext(): Boolean = currentIndex < size - 1
        override fun hasPrevious(): Boolean = currentIndex > startIndex

        override fun next(): DailySchedulePack = get(++currentIndex)
        override fun nextIndex(): Int = currentIndex + 1

        override fun previous(): DailySchedulePack = get(--currentIndex)
        override fun previousIndex(): Int = currentIndex - 1

    }
}