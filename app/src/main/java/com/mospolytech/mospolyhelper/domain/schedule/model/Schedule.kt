package com.mospolytech.mospolyhelper.domain.schedule.model

import com.beust.klaxon.Json
import java.time.LocalDate
import java.time.LocalDateTime


data class Schedule(
    val dailySchedules: List<List<Lesson>>,
    @Json(ignored = true) val lastUpdate: LocalDateTime,
    val group: Group,
    @Json(ignored = true) val isSession: Boolean,
    val dateFrom: LocalDate,
    val dateTo: LocalDate
) {
    class Builder(
        private var dailySchedules: List<List<Lesson>>,
        private var lastUpdate: LocalDateTime? = null,
        private var group: Group = Group.empty,
        private var isSession: Boolean,
        private var dateFrom: LocalDate? = null,
        private var dateTo: LocalDate? = null
    ) {
        fun dailySchedules(dailySchedules: List<List<Lesson>>) = apply { this.dailySchedules = dailySchedules }

        fun lastUpdate(lastUpdate: LocalDateTime) = apply { this.lastUpdate = lastUpdate }

        fun group(group: Group) = apply { this.group = group }

        fun isSession(isSession: Boolean) = apply { this.isSession = isSession }

        fun dateFrom(dateFrom: LocalDate) = apply { this.dateFrom = dateFrom }

        fun dateTo(dateTo: LocalDate) = apply { this.dateTo = dateTo }

        fun build(): Schedule {
            var dateFrom = this.dateFrom ?: LocalDate.MAX
            var dateTo = this.dateTo ?: LocalDate.MIN
            if (this.dateFrom == null || this.dateTo == null) {
                for (dailySchedule in dailySchedules) {
                    for (lesson in dailySchedule) {
                        if (lesson.dateFrom < dateFrom)
                            dateFrom = lesson.dateFrom;
                        if (lesson.dateTo > dateTo)
                            dateTo = lesson.dateTo;
                    }
                }
            }
            val lastUpdate = lastUpdate ?: LocalDateTime.now()

            return Schedule(
                dailySchedules,
                lastUpdate,
                group,
                isSession,
                dateFrom,
                dateTo
            )
        }
    }


    fun getSchedule(date: LocalDate, filter: Filter = Filter.default) =
        filter.getFiltered(dailySchedules[date.dayOfWeek.value % 7], date)

    class Filter(val sessionFilter: Boolean, val dateFilter: DateFilter) {
        companion object {
            val default =
                Filter(
                    true,
                    DateFilter.Hide
                )
            val none =
                Filter(
                    false,
                    DateFilter.Show
                )
        }
        enum class DateFilter{
            Show,
            Desaturate,
            Hide
        }

        fun getFiltered(dailySchedule: List<Lesson>, date: LocalDate) =
            dailySchedule.filter {
                ((dateFilter != DateFilter.Hide ||
                        ((it.isImportant || it.dateFrom <= date) && date <= it.dateTo)) &&
                        (!sessionFilter ||
                                !it.isImportant || (date in it.dateFrom..it.dateTo)))
            }

        class Builder(
            private var sessionFilter: Boolean? = null,
            private var dateFilter: DateFilter? = null
        ) {
            constructor(filter: Filter) : this(filter.sessionFilter, filter.dateFilter)

            fun sessionFilter(sessionFilter: Boolean) =
                apply { this.sessionFilter = sessionFilter }

            fun dateFilter(dateFilter: DateFilter) =
                apply { this.dateFilter = dateFilter }

            fun build(): Filter {
                val sessionFilter = this.sessionFilter ?: default.sessionFilter
                val dateFilter = this.dateFilter ?: default.dateFilter
                return Filter(
                    sessionFilter,
                    dateFilter
                )
            }
        }
    }

    class AdvancedSearch(
        val lessonTitles: Iterable<String>,
        val lessonTeachers: Iterable<String>,
        val lessonAuditoriums: Iterable<String>,
        val lessonTypes: Iterable<String>
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
            val tempList: List<MutableList<Lesson>> = listOf(mutableListOf(), mutableListOf(),
                mutableListOf(), mutableListOf(), mutableListOf(),
                mutableListOf(), mutableListOf())

            var dateFrom = LocalDate.MIN
            var dateTo = LocalDate.MAX

            for (schedule in schedules) {
                if (schedule == null) {
                    continue
                }
                if (schedule.dateFrom < dateFrom) {
                    dateFrom = schedule.dateFrom;
                }
                if (schedule.dateTo > dateTo) {
                    dateTo = schedule.dateTo;
                }
                for (i in schedule.dailySchedules.indices) {
                    tempList[i].addAll(schedule.dailySchedules[i].asSequence().filter { lesson ->
                        checkFilter(lessonTitles, lesson.title) &&
                                checkFilter(lessonTeachers, lesson.teachers.map { it.getFullName() }) &&
                                checkFilter(lessonAuditoriums, lesson.auditoriums.map { it.title }) &&
                                checkFilter(lessonTypes, lesson.type)
                    })
                }
            }
            tempList.forEach { it.sort() }

            return Schedule(
                tempList,
                LocalDateTime.now(),
                Group.empty,
                false,
                dateFrom,
                dateTo
            )
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
            // if not empty
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