package com.mospolytech.mospolyhelper.repository.schedule

import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.repository.schedule.models.LessonLabelKey
import java.time.LocalDate

class LessonLabelRepository(private val dao: LessonLabelDao) {
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
        return Pair(dao.get(lessonLabelKeyOneDate), dao.get(lessonLabelKeyAllDates))
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
        dao.remove(label, lessonLabelKey)
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
        dao.add(label, lessonLabelKey)
    }

    fun getAllLabels(): Set<String> {
        return dao.getAll().toSortedSet()
    }
}