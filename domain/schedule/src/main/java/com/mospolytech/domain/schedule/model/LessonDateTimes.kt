package com.mospolytech.domain.schedule.model

import com.mospolytech.domain.base.utils.converters.LocalDateConverter
import kotlinx.serialization.Serializable
import com.mospolytech.domain.base.utils.converters.LocalTimeConverter
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class LessonDateTimes(
    val lesson: Lesson,
    val time: List<LessonDateTime>
): Comparable<LessonDateTimes> {
    override fun compareTo(other: LessonDateTimes): Int {
        return lesson.compareTo(other.lesson)
    }
}

@Serializable
data class LessonDateTime(
    @Serializable(with = LocalDateConverter::class)
    val date: LocalDate,
    val time: LessonTime
)