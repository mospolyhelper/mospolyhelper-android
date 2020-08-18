package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.LessonLabelLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonLabelKey
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class LessonLabelRepository(private val dataSource: LessonLabelLocalDataSource) {
    fun getLabels(lesson: Lesson, date: LocalDate): Pair<Set<String>, Set<String>> {
        val lessonLabelKeyOneDate = LessonLabelKey(
            lesson.order,
            lesson.title,
            lesson.teachers,
            lesson.auditoriums,
            lesson.type,
            lesson.group.title,
            date.dayOfWeek,
            date
        )
        val lessonLabelKeyAllDates = LessonLabelKey(
            lesson.order,
            lesson.title,
            lesson.teachers,
            lesson.auditoriums,
            lesson.type,
            lesson.group.title,
            date.dayOfWeek,
            null
        )
        return Pair(dataSource.get(lessonLabelKeyOneDate), dataSource.get(lessonLabelKeyAllDates))
    }

    fun removeLabel(label: String, lesson: Lesson, date: LocalDate, isOneDate: Boolean) {
        val lessonLabelKey = LessonLabelKey(
            lesson.order,
            lesson.title,
            lesson.teachers,
            lesson.auditoriums,
            lesson.type,
            lesson.group.title,
            date.dayOfWeek,
            if (isOneDate) date else null
        )
        dataSource.remove(label, lessonLabelKey)
    }

    fun addLabel(label: String, lesson: Lesson, date: LocalDate, isOneDate: Boolean) {
        val lessonLabelKey = LessonLabelKey(
            lesson.order,
            lesson.title,
            lesson.teachers,
            lesson.auditoriums,
            lesson.type,
            lesson.group.title,
            date.dayOfWeek,
            if (isOneDate) date else null
        )
        dataSource.add(label, lessonLabelKey)
    }

    fun getAll() = flow { emit(dataSource.getAll()) }

    fun getAllLabels(): Set<String> {
        return dataSource.getAllLabels().toSortedSet()
    }
}