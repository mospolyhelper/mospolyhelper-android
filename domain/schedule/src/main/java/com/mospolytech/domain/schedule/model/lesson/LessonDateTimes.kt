package com.mospolytech.domain.schedule.model.lesson

import com.mospolytech.domain.base.utils.converters.LocalDateConverter
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

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

fun LessonDateTime.toDateTimeRange(): ClosedRange<LocalDateTime> {
    return LocalDateTime.of(date, time.startTime)..LocalDateTime.of(date, time.endTime)
}
