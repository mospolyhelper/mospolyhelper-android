package com.mospolytech.mospolyhelper.domain.schedule.model

import java.time.DayOfWeek
import java.time.LocalDate

data class LessonLabel(val label: String)

data class LessonLabelKey(
    val lessonOrder: Int,
    val lessonTitle: String,
    val lessonTeachers: List<Teacher>,
    val lessonAuditoriums: List<Auditorium>,
    val lessonType: String,
    val groupTitle: String,
    val lessonDay: DayOfWeek,
    val lessonDate: LocalDate?
) {
    companion object {
        val EVERY_LESSON_DATE: LocalDate? = null

        fun from(lesson: Lesson, date: LocalDate, allDates: Boolean): LessonLabelKey {
            return LessonLabelKey(
                lesson.order,
                lesson.title,
                lesson.teachers,
                lesson.auditoriums,
                lesson.type,
                lesson.groups.joinToString { it.title },
                date.dayOfWeek,
                if (allDates) null else date
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LessonLabelKey

        if (lessonOrder != other.lessonOrder) return false
        if (lessonTitle != other.lessonTitle) return false
        if (lessonType != other.lessonType) return false
        if (groupTitle != other.groupTitle) return false
        if (lessonDay != other.lessonDay) return false
        if (lessonDate != other.lessonDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lessonOrder
        result = 31 * result + lessonTitle.hashCode()
        result = 31 * result + lessonType.hashCode()
        result = 31 * result + groupTitle.hashCode()
        result = 31 * result + lessonDay.hashCode()
        result = 31 * result + (lessonDate?.hashCode() ?: 0)
        return result
    }

}