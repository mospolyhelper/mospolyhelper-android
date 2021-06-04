package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SchedulePack(
    private val scheduleUiData: ScheduleUiData
) : List<DailySchedulePack> {
    companion object {
        private const val MAX_COUNT = 400
    }

    override val size: Int
    val dateFrom: LocalDate
    init {
        if (scheduleUiData.schedule == null) {
            size = 0
            dateFrom = LocalDate.now()
        } else {
            val tempSize = scheduleUiData.schedule.dateFrom
                .until(scheduleUiData.schedule.dateTo, ChronoUnit.DAYS).toInt() + 1
            if (tempSize !in 1..MAX_COUNT) {
                size = MAX_COUNT
                dateFrom = LocalDate.now().minusDays((MAX_COUNT / 2).toLong())
            } else {
                size = tempSize
                dateFrom = scheduleUiData.schedule.dateFrom
            }
        }
    }

    private val cachedDays = MutableList<DailySchedulePack?>(size) { null }

    private fun getDateByPosition(position: Int): LocalDate {
        return dateFrom.plusDays(position.toLong())
    }

    override fun isEmpty() = size == 0

    override fun get(index: Int): DailySchedulePack {
        val cachedDay = cachedDays[index]
        if (cachedDay == null) {
            val dailySchedule = DailySchedulePack.Builder()
                .withEmptyLessons(scheduleUiData.settings.showEmptyLessons)
                .withLessonWindows(true)
                .build(
                    scheduleUiData.schedule!!,
                    getDateByPosition(index),
                    scheduleUiData.settings.dateFilter,
                    scheduleUiData.settings.lessonFeatures,
                    { lesson, dayOfWeek, order ->
                        val tagKey = LessonTagKey.fromLesson(lesson, dayOfWeek, order)
                        scheduleUiData.tags.filter { it.lessons.contains(tagKey) }
                    },
                    { lesson ->
                        scheduleUiData.deadlines[lesson.title] ?: emptyList()
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
        private var currentIndex = startIndex

        override fun hasNext(): Boolean = currentIndex < size - 1
        override fun hasPrevious(): Boolean = currentIndex > startIndex

        override fun next(): DailySchedulePack = get(++currentIndex)
        override fun nextIndex(): Int = currentIndex + 1

        override fun previous(): DailySchedulePack = get(--currentIndex)
        override fun previousIndex(): Int = currentIndex - 1

    }
}