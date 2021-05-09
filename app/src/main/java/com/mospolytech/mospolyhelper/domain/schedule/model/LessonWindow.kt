package com.mospolytech.mospolyhelper.domain.schedule.model

data class LessonWindow(
    val previousLessonPlace: LessonPlace,
    val nextLessonPlace: LessonPlace
) : ScheduleItem