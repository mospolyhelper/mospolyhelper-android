package com.mospolytech.mospolyhelper.domain.schedule.utils

import com.mospolytech.mospolyhelper.domain.schedule.model.*
import java.time.LocalDate

object ScheduleUtils {
    fun getWindowsDecorator(lessonPlaces: List<LessonPlace>): List<ScheduleItem> {
        if (lessonPlaces.isEmpty()) return lessonPlaces
        val resList = mutableListOf<ScheduleItem>(lessonPlaces.first())

        var prevOrder = lessonPlaces.first()
        for (i in 1 until lessonPlaces.size) {
            val lessonPlace = lessonPlaces[i]
            val lessonWindow = lessonPlace.time.order - prevOrder.time.order - 1
            if (lessonWindow > 0 || prevOrder.time.order == 2) {
                resList.add(
                    LessonWindow(
                        prevOrder.copy(lessons = emptyList()).time,
                        lessonPlace.copy(lessons = emptyList()).time
                    )
                )
            }
            resList.add(lessonPlace)
            prevOrder = lessonPlace
        }
        return resList
    }

    fun getEmptyPairsDecorator(lessonPlaces: List<LessonPlace>): List<LessonPlace> {
        if (lessonPlaces.isEmpty()) return lessonPlaces
        val lastOrder = lessonPlaces.last().time.order
        val resList = mutableListOf<LessonPlace>()

        for (i in 0..lastOrder) {
            var notFound = true
            for (lessonPlace in lessonPlaces) {
                if (lessonPlace.time.order == i) {
                    notFound = false
                    resList.add(lessonPlace)
                } else if (lessonPlace.time.order > i && notFound) {
                    resList.add(LessonPlace(emptyList(), LessonTime(i, false)))
                }
            }
        }

        return resList
    }

    fun List<LessonPlace>?.getOrderMap(): Map<Int, Boolean> {
        if (this == null) return (0..6).associateWith { false }
        val map = mutableMapOf<Int, Boolean>()
        for (lessonPlace in this) {
            if (lessonPlace.lessons.isNotEmpty()) {
                map[lessonPlace.time.order] = true
            }
        }
        for (i in 0..6) {
            if (i !in map) {
                map[i] = false
            }
        }
        return map
    }
}


fun merge(lessonPlace1: LessonPlace, lessonPlace2: LessonPlace): LessonPlace {
    val lessons = (lessonPlace1.lessons + lessonPlace2.lessons).toSortedSet().toMutableList()

    val newList = mutableListOf<Lesson>()
    for (lesson in lessons) {
        val index = newList.indexOfFirst { it.canMergeByGroup(lesson) }
        if (index == -1) {
            newList += lesson
        } else {
            newList[index] = newList[index].mergeByGroup(lesson)
        }
    }

    return LessonPlace(newList, lessonPlace1.time)
}

fun combine(schedule1: Schedule, schedule2: Schedule): Schedule {
    if (schedule1 == schedule2) return schedule1

    val resList = schedule1.dailySchedules.map { it.toMutableList() }
    for (day in schedule2.dailySchedules.withIndex()) {
        for (lessonPlace in day.value) {
            val index = resList[day.index].indexOfFirst { it.time == lessonPlace.time }
            if (index == -1) {
                resList[day.index] += lessonPlace
            } else {
                resList[day.index][index] = merge(resList[day.index][index], lessonPlace)
            }
        }
    }

    return Schedule.from(resList.map { it.filter{ it.lessons.isNotEmpty() }.sortedBy { it.time } })
}

fun Schedule.getAllTypes(): Set<String> {
    val lessonTypes = HashSet<String>()
    for (dailySchedule in dailySchedules) {
        for (lessonPlace in dailySchedule) {
            for (lesson in lessonPlace.lessons) {
                lessonTypes.add(lesson.type)
            }
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

    val tempList = MutableList(7) { emptyList<LessonPlace>() }

    // !! used because filterTitles = titles == null
    for (i in dailySchedules.indices) {
        tempList[i] = dailySchedules[i].map {
            LessonPlace(
                it.lessons.filter { lesson ->
                (filterTitles || checkFilter(titles!!, lesson.title)) &&
                        (filterTypes || checkFilter(types!!, lesson.type)) &&
                        (filterTeachers || checkFilter(teachers!!, lesson.teachers.map { it.name })) &&
                        (filterGroups || checkFilter(groups!!, lesson.groups.map { it.title })) &&
                        (filterAuditoriums || checkFilter(auditoriums!!, lesson.auditoriums.map { it.title }))

                },
                it.time
            )
        }
    }

    return Schedule.from(tempList.map { it.filter{ it.lessons.isNotEmpty() }.sortedBy { it.time } })
}

fun Schedule.filter(
    filters: ScheduleFilters
): Schedule {
    val filterTitles = filters.titles.isEmpty()
    val filterTypes = filters.types.isEmpty()
    val filterAuditoriums = filters.auditoriums.isEmpty()
    val filterTeachers = filters.teachers.isEmpty()
    val filterGroups = filters.groups.isEmpty()

    val tempList = MutableList(7) { emptyList<LessonPlace>() }

    for (i in dailySchedules.indices) {
        tempList[i] = dailySchedules[i].map {
            LessonPlace(
                it.lessons.filter { lesson ->
                    (filterTitles || checkFilter(filters.titles, lesson.title)) &&
                            (filterTypes || checkFilter(filters.types, lesson.type)) &&
                            (filterTeachers || checkFilter(filters.teachers, lesson.teachers.map { it.name })) &&
                            (filterGroups || checkFilter(filters.groups, lesson.groups.map { it.title })) &&
                            (filterAuditoriums || checkFilter(filters.auditoriums, lesson.auditoriums.map { it.title }))

                },
                it.time
            )
        }
    }

    return Schedule.from(tempList.map { it.filter{ it.lessons.isNotEmpty() }.sortedBy { it.time } })
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

    val tempList = MutableList(7) { mutableListOf<LessonPlace>() }

    for (schedule in this) {
        if (schedule == null) continue
        // !! used because filterTitles = titles == null
        for (i in schedule.dailySchedules.indices) {
            val q = schedule.dailySchedules[i].mapNotNull {
                val lessons = it.lessons.filter { lesson ->
                    (filterTitles || checkFilter(titles!!, lesson.title)) &&
                            (filterTypes || checkFilter(types!!, lesson.type)) &&
                            (filterTeachers || checkFilter(teachers!!, lesson.teachers.map { it.name })) &&
                            (filterGroups || checkFilter(groups!!, lesson.groups.map { it.title })) &&
                            (filterAuditoriums || checkFilter(auditoriums!!, lesson.auditoriums.map { it.title }))

                }
                if (lessons.isEmpty()) {
                    null
                } else {
                    LessonPlace(
                        lessons,
                        it.time
                    )
                }

            }
            tempList[i].addAll(q)
        }
    }

    val resList = List(7) { mutableListOf<LessonPlace>() }

    for (day in tempList.withIndex()) {
        for (lessonPlace in day.value) {
            val index = resList[day.index].indexOfFirst { it.time == lessonPlace.time }
            if (index == -1) {
                resList[day.index] += lessonPlace
            } else {
                resList[day.index][index] = merge(resList[day.index][index], lessonPlace)
            }
        }
    }

    return Schedule.from(resList.map { it.filter{ it.lessons.isNotEmpty() }.sortedBy { it.time } })
}

fun filterByDate(
    dailySchedule: List<LessonPlace>,
    date: LocalDate,
    showEnded: Boolean,
    showCurrent: Boolean,
    showNotStarted: Boolean
): List<LessonPlace> {
    return dailySchedule.map {
        LessonPlace(
            it.lessons.filter {
                if (showEnded && showCurrent && showNotStarted) return@filter true
                if (!showEnded && !showCurrent && !showNotStarted) return@filter true

                if (showEnded && !showCurrent && !showNotStarted) return@filter date > it.dateTo
                if (showEnded && showCurrent && !showNotStarted) return@filter date >= it.dateFrom

                if (!showEnded && !showCurrent && showNotStarted) return@filter date < it.dateFrom
                if (!showEnded && showCurrent && showNotStarted) return@filter date <= it.dateTo

                if (!showEnded && showCurrent && !showNotStarted) return@filter date in it.dateFrom..it.dateTo
                if (showEnded && !showCurrent && showNotStarted) return@filter date !in it.dateFrom..it.dateTo

                return@filter true
            },
            it.time
        )
    }.filter{ it.lessons.isNotEmpty() }
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