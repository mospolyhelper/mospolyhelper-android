package com.mospolytech.domain.schedule.model

import com.mospolytech.domain.base.utils.converters.LocalTimeConverter
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class LessonTime(
    @Serializable(with = LocalTimeConverter::class)
    val startTime: LocalTime,
    @Serializable(with = LocalTimeConverter::class)
    val endTime: LocalTime
) : Comparable<LessonTime> {
    override fun compareTo(other: LessonTime): Int {
        val start = this.startTime.compareTo(other.startTime)
        if (start == 0) {
            val end = this.endTime.compareTo(other.endTime)
            return end
        }
        return start
    }

}