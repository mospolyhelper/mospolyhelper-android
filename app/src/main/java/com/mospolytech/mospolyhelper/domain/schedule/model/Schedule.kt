package com.mospolytech.mospolyhelper.domain.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.utils.filterByDate
import java.time.LocalDate


data class Schedule(
    val dailySchedules: List<List<Lesson>>,
    val dateFrom: LocalDate,
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

    class AdvancedSearch(
        private val lessonTitles: Iterable<String>,
        private val lessonTeachers: Iterable<String>,
        private val lessonAuditoriums: Iterable<String>,
        private val lessonTypes: Iterable<String>
    ) {
        class Builder(
            private var lessonTitles: Iterable<String> = listOf(),
            private var lessonTeachers: Iterable<String> = listOf(),
            private var lessonAuditoriums: Iterable<String> = listOf(),
            private var lessonTypes: Iterable<String> = listOf()
        ) {

            fun lessonTitles(lessonTitles: Iterable<String>) =
                apply { this.lessonTitles = lessonTitles }

            fun lessonTeachers(lessonTeachers: Iterable<String>) =
                apply { this.lessonTeachers = lessonTeachers }

            fun lessonAuditoriums(lessonAuditoriums: Iterable<String>) =
                apply { this.lessonAuditoriums = lessonAuditoriums }

            fun lessonTypes(lessonTypes: Iterable<String>) =
                apply { this.lessonTypes = lessonTypes }

            fun build() =
                AdvancedSearch(
                    lessonTitles,
                    lessonTeachers,
                    lessonAuditoriums,
                    lessonTypes
                )
        }

        fun getFiltered(schedules: Iterable<Schedule?>): Schedule {
            val tempList: List<MutableList<Lesson>> = listOf(
                mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(),
                mutableListOf(), mutableListOf(), mutableListOf()
            )

            var dateFrom = LocalDate.MIN
            var dateTo = LocalDate.MAX

            for (schedule in schedules) {
                if (schedule == null) {
                    continue
                }
                if (schedule.dateFrom < dateFrom) {
                    dateFrom = schedule.dateFrom
                }
                if (schedule.dateTo > dateTo) {
                    dateTo = schedule.dateTo
                }
                for (i in schedule.dailySchedules.indices) {
                    tempList[i].addAll(
                        schedule.dailySchedules[i].asSequence().filter { lesson ->
                            checkFilter(lessonTitles, lesson.title) &&
                                    checkFilter(
                                        lessonTeachers,
                                        lesson.teachers.map { it.getFullName() }
                                    ) &&
                                    checkFilter(
                                        lessonAuditoriums,
                                        lesson.auditoriums.map { it.title }
                                    ) &&
                                    checkFilter(lessonTypes, lesson.type)
                        }
                    )
                }
            }
            tempList.forEach { it.sort() }

            val tempList2: List<MutableList<Lesson>> = listOf(
                mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(),
                mutableListOf(), mutableListOf(), mutableListOf()
            )

            for (day in tempList.withIndex()) {
                val day2 = tempList2[day.index]
                for (lesson in day.value) {
                    val index = day2.indexOfFirst { isEqualForGroups(lesson, it) }
                    if (index == -1) {
                        day2 += lesson
                    } else {
                        val lesson2 = day2[index]
                        day2[index] = lesson2.copy(groups = lesson2.groups + lesson.groups)
                    }
                }
            }


            return Schedule(
                tempList2,
                dateFrom,
                dateTo
            )
        }

        private fun isEqualForGroups(l1: Lesson, l2: Lesson): Boolean {
            return l1.order == l2.order &&
                    l1.title == l2.title &&
                    l1.auditoriums == l2.auditoriums &&
                    l1.teachers == l2.teachers &&
                    l1.dateFrom == l2.dateFrom &&
                    l1.dateTo == l2.dateTo
        }

        private fun checkFilter(filterList: Iterable<String>, value: String): Boolean {
            val iterator = filterList.iterator()
            if (iterator.hasNext()) {
                do {
                    if (iterator.next() == value) return true
                } while (iterator.hasNext())
                return false
            }
            else {
                return true
            }
        }

        private fun checkFilter(filterList: Iterable<String>, values: Iterable<String>): Boolean {
            val filterIterator = filterList.iterator()
            // if filters are not empty
            if (filterIterator.hasNext()) {
                do {
                    val valueIterator = values.iterator()
                    // return if empty
                    if (!valueIterator.hasNext()) return false
                    val curFilter = filterIterator.next()
                    do {
                        if (valueIterator.next() == curFilter) {
                            return true
                        }
                    } while (valueIterator.hasNext())
                } while (filterIterator.hasNext())

                return false
            }
            else {
                return true
            }
        }
    }
}

class SchedulePackList(
    val schedules: Iterable<Schedule?>,
    val lessonTitles: MutableSet<String>,
    val lessonTeachers: MutableSet<String>,
    val lessonAuditoriums: MutableSet<String>,
    val lessonTypes: MutableSet<String>
)