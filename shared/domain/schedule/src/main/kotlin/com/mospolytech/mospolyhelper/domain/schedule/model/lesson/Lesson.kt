package com.mospolytech.mospolyhelper.domain.schedule.model.lesson

import com.mospolytech.mospolyhelper.domain.common.model.Location
import java.time.LocalDate
import java.time.LocalTime

data class Lesson(
    val id: String,
    val time: LessonTime,
    val title: String,
    val type: String,
    val place: List<LessonPlace>,
    val groups: List<Group>,
    val teachers: List<Teacher>,
    val dateFrom: LocalDate,
    val dateTo: LocalDate
)

data class LessonTime(
    val start: LocalTime,
    val end: LocalTime
)

sealed class LessonPlace(
    open val id: String,
    open val title: String,
    open val description: String,
    open val imageUrl: String
) {
    data class Local(
        override val id: String,
        override val title: String,
        override val description: String,
        override val imageUrl: String,
        val location: Location
    ) : LessonPlace(id, title, description, imageUrl)

    data class Online(
        override val id: String,
        override val title: String,
        override val description: String,
        override val imageUrl: String,
        val url: String
    ) : LessonPlace(id, title, description, imageUrl)
}

data class Teacher(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String
)

data class Group(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String
)
