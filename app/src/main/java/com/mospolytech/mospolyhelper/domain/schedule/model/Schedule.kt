package com.mospolytech.mospolyhelper.domain.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonDateFilter
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonPlace
import com.mospolytech.mospolyhelper.domain.schedule.utils.filterByDate
import com.mospolytech.mospolyhelper.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Schedule(
    val dailySchedules: List<List<LessonPlace>>,
    @Serializable(with = LocalDateSerializer::class)
    val dateFrom: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val dateTo: LocalDate
) {
    companion object {
        fun from(dailySchedules: List<List<LessonPlace>>): Schedule {
            val today = LocalDate.now()
            val minDateFrom = today.minusDays(200L)
            val maxDateTo = today.plusDays(200L)
            val requiredDateRange = minDateFrom..maxDateTo


            var dateFrom = LocalDate.MAX
            var dateTo = LocalDate.MIN
            for (dailySchedule in dailySchedules) {
                for (lessonPlace in dailySchedule) {
                    for (lesson in lessonPlace.lessons) {
                        if (lesson.dateFrom < dateFrom)
                            dateFrom = lesson.dateFrom
                        if (lesson.dateTo > dateTo)
                            dateTo = lesson.dateTo
                    }
                }
            }

            if (dateFrom !in requiredDateRange) {
                dateFrom = minDateFrom
            }

            if (dateTo !in requiredDateRange) {
                dateTo = maxDateTo
            }

            return Schedule(
                dailySchedules,
                dateFrom,
                dateTo
            )
        }
    }

    fun getLessons(
        date: LocalDate,
        showEnded: Boolean = false,
        showCurrent: Boolean = true,
        showNotStarted: Boolean = false
    ) = filterByDate(
        dailySchedules[date.dayOfWeek.value % 7],
        date,
        showEnded,
        showCurrent,
        showNotStarted
    )

    fun getLessons(
        date: LocalDate,
        lessonDateFilter: LessonDateFilter = LessonDateFilter.Default
    ) = filterByDate(
        dailySchedules[date.dayOfWeek.value % 7],
        date,
        lessonDateFilter.showEndedLessons,
        lessonDateFilter.showCurrentLessons,
        lessonDateFilter.showNotStartedLessons
    )
}