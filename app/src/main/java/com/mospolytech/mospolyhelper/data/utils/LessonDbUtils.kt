package com.mospolytech.mospolyhelper.data.utils

import com.mospolytech.mospolyhelper.data.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import java.time.DayOfWeek

fun LessonWithFeaturesDb.toLesson(): Lesson {
    return Lesson(
        lesson.title,
        lesson.type,
        teachers.map { it.toTeacher() },
        auditoriums.map { it.toAuditorium() },
        groups.map { it.toGroup() },
        lesson.dateFrom,
        lesson.dateTo
    )
}

fun Lesson.toLessonDb(dayOfWeek: DayOfWeek, lessonTime: LessonTime): LessonDb {
    return LessonDb(
        0,
        dayOfWeek,
        lessonTime.order,
        lessonTime.isEvening,
        title,
        type,
        dateFrom,
        dateTo
    )
}

fun Lesson.toLessonWithFeaturesDb(dayOfWeek: DayOfWeek, lessonTime: LessonTime): LessonWithFeaturesDb {
    return LessonWithFeaturesDb(
        toLessonDb(dayOfWeek, lessonTime),
        teachers.map { it.toTeacherDb() },
        groups.map { it.toGroupDb() },
        auditoriums.map { it.toAuditoriumDb() }
    )
}

fun Teacher.toTeacherDb(): TeacherDb {
    return TeacherDb(name)
}

fun Group.toGroupDb(): GroupDb {
    return GroupDb(title, isEvening)
}

fun Auditorium.toAuditoriumDb(): AuditoriumDb {
    return AuditoriumDb(
        0,
        title,
        type,
        url,
        color
    )
}

fun TeacherDb.toTeacher(): Teacher {
    return Teacher(teacherName)
}

fun GroupDb.toGroup(): Group {
    return Group(groupTitle, isEvening)
}

fun AuditoriumDb.toAuditorium(): Auditorium {
    return Auditorium(title, type, color, url)
}