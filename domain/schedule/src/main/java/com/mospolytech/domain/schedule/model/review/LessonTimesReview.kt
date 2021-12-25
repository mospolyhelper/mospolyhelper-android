package com.mospolytech.domain.schedule.model.review

import com.mospolytech.domain.base.utils.converters.LocalDateConverter
import com.mospolytech.domain.schedule.model.lesson.LessonTime
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
data class LessonTimesReview(
    val lessonTitle: String,
    val days: List<LessonTimesReviewByType>
)

@Serializable
data class LessonTimesReviewByType(
    val lessonType: String,
    val days: List<LessonReviewDay>
): Comparable<LessonTimesReviewByType> {
    override operator fun compareTo(other: LessonTimesReviewByType): Int {
        return this.lessonType.compareTo(other.lessonType)
    }
}


@Serializable
data class LessonReviewDay(
    val dayOfWeek: DayOfWeek,
    val time: LessonTime,
    @Serializable(with = LocalDateConverter::class)
    val dateFrom: LocalDate,
    @Serializable(with = LocalDateConverter::class)
    val dateTo: LocalDate
): Comparable<LessonReviewDay> {
    override operator fun compareTo(other: LessonReviewDay): Int {
        val dateFromComp = this.dateFrom.compareTo(other.dateFrom)
        if (dateFromComp != 0) return dateFromComp
        val dateToComp = this.dateTo.compareTo(other.dateTo)
        if (dateToComp != 0) return dateToComp
        val dayOfWeekComp = this.dayOfWeek.compareTo(other.dayOfWeek)
        if (dayOfWeekComp != 0) return dayOfWeekComp
        val timeComp = this.time.compareTo(other.time)
        return timeComp
    }
}