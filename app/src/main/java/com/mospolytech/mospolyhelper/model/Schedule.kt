package com.mospolytech.mospolyhelper.model

import android.util.Log
import com.mospolytech.mospolyhelper.TAG
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

            return  Schedule(
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
            val default by lazy { Filter(true, DateFilter.Hide) }
            val none by lazy { Filter(false, DateFilter.Show) }
        }
        enum class DateFilter{
            Show,
            Desaturate,
            Hide
        }

        fun getFiltered(dailySchedule: List<Lesson>, date: Calendar) =
            dailySchedule.filter {
                ((dateFilter != DateFilter.Hide ||
                        ((it.dateFrom <= date || it.isImportant) && date <= it.dateTo)) &&
                        (!sessionFilter ||
                                !it.isImportant || (it.dateFrom <= date && date <= it.dateTo)))
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

data class Group(
    val title: String,
    val dateFrom: Calendar,
    val dateTo: Calendar,
    val isEvening: Boolean,
    val comment: String,
    val course: Int
    ) {
    companion object {
        val empty by lazy {
            Group(
                "",
                Calendar.getInstance().apply { time = Date(Long.MIN_VALUE) },
                Calendar.getInstance().apply { time = Date(Long.MAX_VALUE) },
                false,
                "",
                0
            )
        }
    }
}

data class Lesson(
    val order: Int,
    val title: String,
    val teachers: List<Teacher>,
    val dateFrom: Calendar,
    val dateTo: Calendar,
    val auditoriums: List<Auditorium>,
    val type: String,
    val group: Group
) : Comparable<Lesson> {
    companion object {
        const val COURSE_PROJECT = "кп"
        const val EXAM = "экзамен"
        const val CREDIT = "зачет"
        const val CREDIT_WITH_MARK = "зсо"
        const val EXAMINATION_SHOW = "эп"
        const val CONSULTATION = "консультация"
        const val LABORATORY = "лаб"
        const val PRACTICE = "практика"
        const val LECTURE = "лекция"
        const val OTHER = "другое"

        fun getEmpty(order: Int) =
            Lesson(
                order,
                "",
                emptyList(),
                Calendar.getInstance().apply { time = Date(Long.MIN_VALUE) },
                Calendar.getInstance().apply { time = Date(Long.MAX_VALUE) },
                emptyList(),
                "",
                Group.empty
            )

        fun getOrder(time: Calendar, groupIsEvening: Boolean): Int =
            if (time > Time.thirdPair.second) when {
                time <= Time.fourthPair.second -> 3
                time <= Time.fifthPair.second -> 4
                groupIsEvening -> if (time <= Time.sixthPairEvening.second) 5 else 6
                else -> if (time <= Time.sixthPair.second) 5 else 6
            }
            else when {
                time > Time.secondPair.second -> 2
                time > Time.firstPair.second -> 1
                else -> 0
            }

    }

    class Time {
        companion object {
            val firstPair = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 9)
                    set(Calendar.MINUTE, 0)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 10)
                    set(Calendar.MINUTE, 30)
                }
            )
            val secondPair = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 10)
                    set(Calendar.MINUTE, 40)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 12)
                    set(Calendar.MINUTE, 10)
                }
            )
            val thirdPair = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 12)
                    set(Calendar.MINUTE, 20)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 13)
                    set(Calendar.MINUTE, 50)
                }
            )
            val fourthPair = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 14)
                    set(Calendar.MINUTE, 30)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 16)
                    set(Calendar.MINUTE, 0)
                }
            )
            val fifthPair = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 16)
                    set(Calendar.MINUTE, 10)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 17)
                    set(Calendar.MINUTE, 40)
                }
            )
            val sixthPair = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 17)
                    set(Calendar.MINUTE, 50)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 19)
                    set(Calendar.MINUTE, 20)
                }
            )
            val sixthPairEvening = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 18)
                    set(Calendar.MINUTE, 20)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 19)
                    set(Calendar.MINUTE, 40)
                }
            )

            val firstPairStr by lazy { Pair("09:00", "10:30") }
            val secondPairStr by lazy { Pair("10:40", "12:10") }
            val thirdPairStr by lazy { Pair("12:20", "13:50") }
            val fourthPairStr by lazy { Pair("14:30", "16:00") }
            val fifthPairStr by lazy { Pair("16:10", "17:40") }
            val sixthPairStr by lazy { Pair("17:50", "19:20") }
            val sixthPairEveningStr by lazy { Pair("18:20", "19:40") }
            val seventhPairStr by lazy { Pair("19:30", "21:00") }
            val seventhPairEveningStr by lazy { Pair("19:50", "21:10") }
        }
    }

    val isEmpty = title.isEmpty() && type.isEmpty()

    val isImportant =
        type.contains(EXAM, true) ||
                type.contains(CREDIT, true) ||
                type.contains(COURSE_PROJECT, true) ||
                type.contains(CREDIT_WITH_MARK, true) ||
                type.contains(EXAMINATION_SHOW, true)

    val time = when (order) {
        0 -> Time.firstPairStr
        1 -> Time.secondPairStr
        2 -> Time.thirdPairStr
        3 -> Time.fourthPairStr
        4 -> Time.fifthPairStr
        5 -> if (group.isEvening) Time.sixthPairEveningStr else Time.sixthPairStr
        6 -> if (group.isEvening) Time.seventhPairEveningStr else Time.seventhPairStr
        else -> {
            Log.e(TAG, "Wrong order number of lesson")
            Pair("Ошибка", "номера занятия")
        }
    }

    fun equalsTime(lesson: Lesson) =
        order == lesson.order && group.isEvening == lesson.group.isEvening

    override fun compareTo(other: Lesson) = when {
        order != other.order -> order.compareTo(other.order)
        group.isEvening == other.group.isEvening -> group.title.compareTo(other.group.title)
        group.isEvening -> 1
        else -> -1
    }
}

data class Teacher(val names: List<String>) {
    companion object {
        fun fromFullName(name: String) =
            Teacher(
                name.replace(" - ", "-")
                    .replace(" -", "-")
                    .replace("- ", "-")
                    .split(" ")
                    .filter { it.isNotEmpty() }
            )
        }

    fun getFullName() = names.joinToString(" ")

    fun getShortName(): String {
        if (names.isEmpty())
            return ""

        val isVacancy = names.any { it.contains("вакансия", true) }

        return if (isVacancy || (names.first().length > 1) && (names.first().let { it[0].isLowerCase() == it[1].isLowerCase() })) {
            names.joinToString("\u00A0")
        } else {
            val shortName = StringBuilder(names.first())
            for (i in names.indices) {
                shortName.append("\u00A0")
                    .append(names[i][0])
                    .append('.')
            }
            shortName.toString()
        }
    }
}

data class Auditorium(val title: String, val color: String)