package com.mospolytech.domain.base.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun LocalDate.getFloorMonday(): LocalDate {
    val currentValue = this.dayOfWeek.value
    val mondayValue = DayOfWeek.MONDAY.value
    return this.plusDays((mondayValue - currentValue).toLong())
}

fun LocalDate.getCeilSunday(): LocalDate {
    val currentValue = this.dayOfWeek.value
    val sunday = DayOfWeek.SUNDAY.value
    return this.plusDays((sunday - currentValue).toLong())
}

class DayIterator(
    private val dateFrom: LocalDate,
    private val dateTo: LocalDate
): Iterable<LocalDate> {
    override fun iterator() = Iterator()
    inner class Iterator : kotlin.collections.Iterator<LocalDate> {
        var currentDate: LocalDate? = null

        override fun hasNext(): Boolean {
            val currentDate = currentDate ?: dateFrom
            return currentDate.until(dateTo, ChronoUnit.DAYS) > 0
        }

        override fun next(): LocalDate {
            val newDate = currentDate?.let { it.plusDays(1) } ?: dateFrom
            this.currentDate = newDate
            return newDate
        }
    }
}

class WeekIterator(
    private val firstMonday: LocalDate,
    private val lastSunday: LocalDate
): Iterable<DayIterator> {
    private val totalWeeks = (firstMonday.until(lastSunday, ChronoUnit.DAYS) + 1) / 7

    override fun iterator() = Iterator()
    inner class Iterator : kotlin.collections.Iterator<DayIterator> {
        var currentWeek: Int? = null

        override fun hasNext(): Boolean {
            val currentWeek = currentWeek ?: 0
            return currentWeek <= totalWeeks
        }

        override fun next(): DayIterator {
            val newWeek = currentWeek?.let { it + 1 } ?: 0
            currentWeek = newWeek
            val monday = firstMonday.plusDays(newWeek * 7L)
            val sunday = monday.plusDays(6L)
            return DayIterator(
                monday,
                sunday
            )
        }

    }
}