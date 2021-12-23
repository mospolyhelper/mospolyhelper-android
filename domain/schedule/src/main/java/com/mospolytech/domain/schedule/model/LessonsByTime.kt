package com.mospolytech.domain.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonsByTime(
    val time: LessonTime,
    val lessons: List<Lesson>
)