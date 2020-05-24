package com.mospolytech.mospolyhelper.repository.models.schedule

import java.util.*


data class Schedule(
    val dailySchedules: List<List<Lesson>>,
    val lastUpdate: Calendar,
    val group: Group,
    val isSession: Boolean,
    val dateFrom: Calendar,
    val dateTo: Calendar
) {
    class Builder(
        private var dailySchedules: List<List<Lesson>>,
        private var lastUpdate: Calendar? = null,
        private var group: Group = Group.empty,
        private var isSession: Boolean,
        private var dateFrom: Calendar? = null,
        private var dateTo: Calendar? = null
    ) {
        fun dailySchedules(dailySchedules: List<List<Lesson>>) = apply { this.dailySchedules = dailySchedules }

        fun lastUpdate(lastUpdate: Calendar) = apply { this.lastUpdate = lastUpdate }

        fun group(group: Group) = apply { this.group = group }

        fun isSession(isSession: Boolean) = apply { this.isSession = isSession }

        fun dateFrom(dateFrom: Calendar) = apply { this.dateFrom = dateFrom }

        fun dateTo(dateTo: Calendar) = apply { this.dateTo = dateTo }

        fun build(): Schedule {
            var dateFrom = this.dateFrom ?: Calendar.getInstance().apply { time = Date(Long.MIN_VALUE) }
            var dateTo = this.dateTo ?: Calendar.getInstance().apply { time = Date(Long.MAX_VALUE) }
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
            val lastUpdate = lastUpdate ?: Calendar.getInstance()

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


    fun getSchedule(date: Calendar, filter: Filter = Filter.default) =
        filter.getFiltered(dailySchedules[date.get(Calendar.DAY_OF_WEEK)], date)

    class Filter(val sessionFilter: Boolean, val dateFilter: DateFilter) {
        companion object {
            val default by lazy {
                Filter(
                    true,
                    DateFilter.Hide
                )
            }
            val none by lazy {
                Filter(
                    false,
                    DateFilter.Show
                )
            }
        }
        enum class DateFilter{
            Show,
            Desaturate,
            Hide
        }

        fun getFiltered(dailySchedule: List<Lesson>, date: Calendar) =
            dailySchedule.filter {
                ((dateFilter != DateFilter.Hide ||
                        ((it.isImportant || it.dateFrom <= date) && date <= it.dateTo)) &&
                        (!sessionFilter ||
                                !it.isImportant || (date in it.dateFrom..it.dateTo)))
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

        fun getFiltered(schedules: Iterable<Schedule>): Schedule {
            val tempList: List<MutableList<Lesson>> = listOf(mutableListOf(), mutableListOf(),
                mutableListOf(), mutableListOf(), mutableListOf(),
                mutableListOf(), mutableListOf())

            var dateFrom = Calendar.getInstance().apply { time = Date(Long.MIN_VALUE) }
            var dateTo = Calendar.getInstance().apply { time = Date(Long.MAX_VALUE) }

            for (schedule in schedules) {
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

            return Schedule(
                tempList,
                Calendar.getInstance(),
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
            val iterator = filterList.iterator()
            if (iterator.hasNext()) {
                val valueIterator = values.iterator()
                if (!valueIterator.hasNext()) return false
                do {
                    var valuesContainCurrentFilter = false
                    do {
                        if (valueIterator.next() == iterator.next()) {
                            valuesContainCurrentFilter = true
                            break
                        }
                    } while (valueIterator.hasNext())

                    if (valuesContainCurrentFilter) return true
                } while (iterator.hasNext())

                return false
            }
            else {
                return true
            }
        }
    }
}