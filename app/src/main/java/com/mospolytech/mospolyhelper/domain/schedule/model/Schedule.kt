package com.mospolytech.mospolyhelper.domain.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.utils.filterByDate
import com.mospolytech.mospolyhelper.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Schedule(
    val dailySchedules: List<List<Lesson>>,
    @Serializable(with = LocalDateSerializer::class)
    val dateFrom: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val dateTo: LocalDate
) {
    companion object {
        fun from(dailySchedules: List<List<Lesson>>): Schedule {
            var dateFrom = LocalDate.MAX
            var dateTo = LocalDate.MIN
            for (dailySchedule in dailySchedules) {
                for (lesson in dailySchedule) {
                    if (lesson.dateFrom < dateFrom)
                        dateFrom = lesson.dateFrom;
                    if (lesson.dateTo > dateTo)
                        dateTo = lesson.dateTo;
                }
            }

            return Schedule(
                dailySchedules,
                dateFrom,
                dateTo
            )
        }
    }

    fun getSchedule(
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

    fun getScheduleCount(date: LocalDate): Int {
        val dailySchedule = getSchedule(date)
        val orders = mutableSetOf<Int>()
        for (lesson in dailySchedule) {
            orders.add(lesson.order)
        }
        return orders.size
    }
}

class SchedulePackList(
    val schedules: Iterable<Schedule?>,
    val lessonTitles: MutableSet<String>,
    val lessonTeachers: MutableSet<String>,
    val lessonAuditoriums: MutableSet<String>,
    val lessonTypes: MutableSet<String>
)