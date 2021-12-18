package com.mospolytech.domain.schedule.model

import com.mospolytech.domain.base.utils.converters.LocalDateConverter
import kotlinx.serialization.Serializable
import com.mospolytech.domain.base.utils.converters.LocalTimeConverter
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class LessonsByTime(
    val time: LessonTime,
    val lessons: List<Lesson>
)