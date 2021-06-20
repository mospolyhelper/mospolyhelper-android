package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.utils.ScheduleUtils.getOrderMap
import java.time.LocalDate

class ScheduleWeekUiData(
    private val schedule: Schedule?,
    private val dateFrom: LocalDate
) : List<ScheduleDayUiData> {

    override val size: Int = 7

    private val cachedDays = MutableList<ScheduleDayUiData?>(size) { null }

    private fun getDateByPosition(position: Int): LocalDate {
        return dateFrom.plusDays(position.toLong())
    }

    override fun isEmpty() = size == 0

    override fun get(index: Int): ScheduleDayUiData {
        val cachedDay = cachedDays[index]
        return if (cachedDay == null) {
            val date = getDateByPosition(index)
            val lessonMap = schedule?.getLessons(date).getOrderMap()
            val item = ScheduleDayUiData(date, lessonMap)
            cachedDays[index] = item
            item
        } else {
            cachedDay
        }
    }


    override fun indexOf(element: ScheduleDayUiData): Int {
        for ((index, item) in this.withIndex()) {
            if (element == item)
                return index
        }
        return -1
    }

    override fun contains(element: ScheduleDayUiData): Boolean =
        this.indexOf(element) != -1

    override fun containsAll(elements: Collection<ScheduleDayUiData>): Boolean =
        this.all { contains(it) }

    override fun iterator(): Iterator<ScheduleDayUiData> =
        ScheduleListIterator()

    override fun lastIndexOf(element: ScheduleDayUiData): Int {
        for (i in size - 1 downTo 0) {
            if (element == get(i))
                return i
        }
        return -1
    }

    override fun listIterator(): ListIterator<ScheduleDayUiData> =
        ScheduleListIterator()

    override fun listIterator(index: Int): ListIterator<ScheduleDayUiData> =
        ScheduleListIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<ScheduleDayUiData> {
        return MutableList(toIndex - fromIndex) { get(fromIndex + it) }
    }

    private inner class ScheduleListIterator(
        private val startIndex: Int = 0
    ) : ListIterator<ScheduleDayUiData> {
        private var currentIndex = startIndex - 1

        override fun hasNext(): Boolean = currentIndex < size - 1
        override fun hasPrevious(): Boolean = currentIndex > startIndex

        override fun next(): ScheduleDayUiData = get(++currentIndex)
        override fun nextIndex(): Int = currentIndex + 1

        override fun previous(): ScheduleDayUiData = get(--currentIndex)
        override fun previousIndex(): Int = currentIndex - 1

    }
}