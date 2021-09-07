package com.mospolytech.mospolyhelper.utils

import java.time.DayOfWeek
import java.time.LocalDate

fun LocalDate.getOfThisWeek(day: DayOfWeek): LocalDate {
    val currentDayOfWeek = this.dayOfWeek.value
    val targetDayOfWeek = day.value
    val diff = targetDayOfWeek - currentDayOfWeek
    return this.plusDays(diff.toLong())
}