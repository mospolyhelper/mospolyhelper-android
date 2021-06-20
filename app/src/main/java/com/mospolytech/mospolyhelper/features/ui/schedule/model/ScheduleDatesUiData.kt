package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ScheduleDatesUiData(
    private val schedule: Schedule?
) : List<ScheduleWeekUiData> {
    override val size: Int
    val dates: ClosedRange<LocalDate>

    init {
        if (schedule != null) {
            dates = getMondayOfWeek(schedule.dateFrom)..getSundayOfWeek(schedule.dateTo)
            size = (dates.start.until(dates.endInclusive, ChronoUnit.DAYS) / 7L + 1).toInt()
        } else {
            size = 1
            dates = getWeek(LocalDate.now())
        }
    }

    private val cachedDays = MutableList<ScheduleWeekUiData?>(size) { null }

    private fun getMondayOfWeek(date: LocalDate): LocalDate {
        return date.plusDays(-date.dayOfWeek.value + 1L)
    }

    private fun getSundayOfWeek(date: LocalDate): LocalDate {
        return date.plusDays(7L - date.dayOfWeek.value)
    }

    private fun getWeek(date: LocalDate): ClosedRange<LocalDate> {
        return date.plusDays(-date.dayOfWeek.value + 1L)..date.plusDays(7L - date.dayOfWeek.value)
    }

    fun getDateByPosition(position: Int): LocalDate {
        return dates.start.plusDays(position.toLong())
    }

    override fun isEmpty() = size == 0

    override fun get(index: Int): ScheduleWeekUiData {
        val cachedDay = cachedDays[index]
        if (cachedDay == null) {
            val weekPack = ScheduleWeekUiData(schedule, dates.start.plusDays(index * 7L))
            cachedDays[index] = weekPack

            return weekPack
        }
        else {
            return cachedDay
        }
    }


    override fun indexOf(element: ScheduleWeekUiData): Int {
        for ((index, item) in this.withIndex()) {
            if (element == item)
                return index
        }
        return -1
    }

    override fun contains(element: ScheduleWeekUiData): Boolean =
        this.indexOf(element) != -1

    override fun containsAll(elements: Collection<ScheduleWeekUiData>): Boolean =
        this.all { contains(it) }

    override fun iterator(): Iterator<ScheduleWeekUiData> =
        ScheduleListIterator()

    override fun lastIndexOf(element: ScheduleWeekUiData): Int {
        for (i in size - 1 downTo 0) {
            if (element == get(i))
                return i
        }
        return -1
    }

    override fun listIterator(): ListIterator<ScheduleWeekUiData> =
        ScheduleListIterator()

    override fun listIterator(index: Int): ListIterator<ScheduleWeekUiData> =
        ScheduleListIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<ScheduleWeekUiData> {
        return MutableList(toIndex - fromIndex) { get(fromIndex + it) }
    }

    private inner class ScheduleListIterator(
        private val startIndex: Int = 0
    ) : ListIterator<ScheduleWeekUiData> {
        private var currentIndex = startIndex - 1

        override fun hasNext(): Boolean = currentIndex < size - 1
        override fun hasPrevious(): Boolean = currentIndex > startIndex

        override fun next(): ScheduleWeekUiData = get(++currentIndex)
        override fun nextIndex(): Int = currentIndex + 1

        override fun previous(): ScheduleWeekUiData = get(--currentIndex)
        override fun previousIndex(): Int = currentIndex - 1

    }
}