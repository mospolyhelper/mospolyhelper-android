package com.mospolytech.domain.schedule.model

import com.mospolytech.domain.base.utils.converters.LocalDateConverter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
data class LessonTimesReview(
    val lessonTitle: String,
    val days: List<LessonReviewDay>
)
@Serializable
sealed class LessonReviewDay: Comparable<LessonReviewDay> {
    @Serializable
    @SerialName("regular")
    data class Regular(
        val lessonType: String,
        val dayOfWeek: DayOfWeek,
        val time: LessonTime,
        @Serializable(with = LocalDateConverter::class)
        val dateFrom: LocalDate,
        @Serializable(with = LocalDateConverter::class)
        val dateTo: LocalDate
    ) : LessonReviewDay() {
        operator fun compareTo(other: Regular): Int {
            val dateFromComp = this.dateFrom.compareTo(other.dateFrom)
            if (dateFromComp != 0) return dateFromComp
            val lessonTypeComp = this.lessonType.compareTo(other.lessonType)
            if (lessonTypeComp != 0) return lessonTypeComp
            val dateToComp = this.dateTo.compareTo(other.dateTo)
            if (dateToComp != 0) return dateToComp
            val dayOfWeekComp = this.dayOfWeek.compareTo(other.dayOfWeek)
            if (dayOfWeekComp != 0) return dayOfWeekComp
            val timeComp = this.time.compareTo(other.time)
            return timeComp
        }
    }

    @Serializable
    @SerialName("single")
    data class Single(
        val lessonType: String,
        @Serializable(with = LocalDateConverter::class)
        val date: LocalDate,
        val time: LessonTime
    ) : LessonReviewDay() {
        operator fun compareTo(other: Single): Int {
            val dateComp = this.date.compareTo(other.date)
            if (dateComp != 0) return dateComp
            val lessonTypeComp = this.lessonType.compareTo(other.lessonType)
            if (lessonTypeComp != 0) return lessonTypeComp
            val timeComp = this.time.compareTo(other.time)
            return timeComp
        }
    }


    override fun compareTo(other: LessonReviewDay): Int {
        return when (this) {
            is Single -> if (other is Single) this.compareTo(other) else 1
            is Regular -> if (other is Regular) this.compareTo(other) else -1
        }
    }
}