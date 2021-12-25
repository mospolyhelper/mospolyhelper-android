package com.mospolytech.domain.schedule.model.schedule

import com.mospolytech.domain.schedule.model.lesson.Lesson
import kotlinx.serialization.Serializable
import com.mospolytech.domain.schedule.model.lesson.LessonTime

@Serializable
data class LessonsByTime(
    val time: LessonTime,
    val lessons: List<Lesson>
)