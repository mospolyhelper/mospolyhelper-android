package com.mospolytech.mospolyhelper.domain.schedule.utils

import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import java.time.LocalDate

fun combine(schedule1: Schedule?, schedule2: Schedule?): Schedule? {
    if (schedule1 == null) return schedule2
    if (schedule2 == null) return schedule1
    if (schedule1 == schedule2) return schedule1

    val resList = schedule1.dailySchedules
        .zip(schedule2.dailySchedules) { day1, day2 ->
            (day1 + day2).sorted()
        }
    val dateFrom = if (schedule1.dateFrom < schedule2.dateFrom) schedule1.dateFrom else schedule2.dateFrom
    val dateTo = if (schedule1.dateTo < schedule2.dateTo) schedule1.dateTo else schedule2.dateTo

    return Schedule(resList, dateFrom, dateTo)
}

fun Schedule.getAllTypes(): Set<String> {
    val lessonTypes = HashSet<String>()
    for (dailySchedule in dailySchedules) {
        for (lesson in dailySchedule) {
            lessonTypes.add(lesson.type)
        }
    }
    return lessonTypes
}

fun Schedule.filter(
    titles: Set<String>? = null,
    types: Set<String>? = null,
    auditoriums: Set<String>? = null,
    teachers: Set<String>? = null,
    groups: Set<String>? = null
): Schedule {
    val filterTitles = titles == null
    val filterTypes = types == null
    val filterAuditoriums = auditoriums == null
    val filterTeachers = teachers == null
    val filterGroups = groups == null

    val tempList: List<MutableList<Lesson>> = listOf(
        mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(), mutableListOf()
    )

    // !! used because filterTitles = titles == null
    for (i in dailySchedules.indices) {
        tempList[i].addAll(dailySchedules[i].asSequence().filter { lesson ->
            (filterTitles || checkFilter(titles!!, lesson.title)) &&
                    (filterTypes || checkFilter(types!!, lesson.type)) &&
                    (filterTeachers || checkFilter(teachers!!, lesson.teachers.map { it.getFullName() })) &&
                    (filterGroups || checkFilter(groups!!, lesson.groups.map { it.title })) &&
                    (filterAuditoriums || checkFilter(auditoriums!!, lesson.auditoriums.map { it.title }))

        })
    }

    return Schedule.from(tempList)
}

fun Iterable<Schedule?>.filter(
    titles: Set<String>? = null,
    types: Set<String>? = null,
    auditoriums: Set<String>? = null,
    teachers: Set<String>? = null,
    groups: Set<String>? = null
): Schedule {
    val filterTitles = titles == null
    val filterTypes = types == null
    val filterAuditoriums = auditoriums == null
    val filterTeachers = teachers == null
    val filterGroups = groups == null

    val tempList: List<MutableList<Lesson>> = listOf(
        mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(), mutableListOf()
    )

    for (schedule in this) {
        if (schedule == null) continue
        // !! used because filterTitles = titles == null
        for (i in schedule.dailySchedules.indices) {
            tempList[i].addAll(schedule.dailySchedules[i].asSequence().filter { lesson ->
                (filterTitles || checkFilter(titles!!, lesson.title)) &&
                        (filterTypes || checkFilter(types!!, lesson.type)) &&
                        (filterTeachers || checkFilter(
                            teachers!!,
                            lesson.teachers.map { it.getFullName() })) &&
                        (filterGroups || checkFilter(groups!!, lesson.groups.map { it.title })) &&
                        (filterAuditoriums || checkFilter(
                            auditoriums!!,
                            lesson.auditoriums.map { it.title }))
            })
        }
    }

    tempList.forEach { it.sort() }

    val tempListNew: List<MutableList<Lesson>> = listOf(
        mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(), mutableListOf()
    )

    for (day in tempList.withIndex()) {
        val dayNew = tempListNew[day.index]
        for (lesson in day.value) {
            val index = dayNew.indexOfFirst { isEqualForGroups(lesson, it) }
            if (index == -1) {
                dayNew += lesson
            } else {
                val lessonEqualForGroups = dayNew[index]
                dayNew[index] = lesson.copy(groups = lessonEqualForGroups.groups + lesson.groups)
            }
        }
    }

    return Schedule.from(tempListNew)
}

fun filterByDate(
    dailySchedule: List<Lesson>,
    date: LocalDate,
    showEnded: Boolean,
    showCurrent: Boolean,
    showNotStarted: Boolean
): List<Lesson> {
    return dailySchedule.filter {
        if (showEnded && showCurrent && showNotStarted) return@filter true
        if (!showEnded && !showCurrent && !showNotStarted) return@filter true

        if (showEnded && !showCurrent && !showNotStarted) return@filter date > it.dateTo
        if (showEnded && showCurrent && !showNotStarted) return@filter date >= it.dateFrom

        if (!showEnded && !showCurrent && showNotStarted) return@filter date < it.dateFrom
        if (!showEnded && showCurrent && showNotStarted) return@filter date <= it.dateTo

        if (!showEnded && showCurrent && !showNotStarted) return@filter date in it.dateFrom..it.dateTo
        if (showEnded && !showCurrent && showNotStarted) return@filter date !in it.dateFrom..it.dateTo

        return@filter true
    }
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

private fun isEqualForGroups(l1: Lesson, l2: Lesson): Boolean {
    return l1.order == l2.order &&
            l1.title == l2.title &&
            l1.auditoriums == l2.auditoriums &&
            l1.teachers == l2.teachers &&
            l1.dateFrom == l2.dateFrom &&
            l1.dateTo == l2.dateTo
}