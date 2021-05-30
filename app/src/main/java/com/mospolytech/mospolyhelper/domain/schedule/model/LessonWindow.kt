package com.mospolytech.mospolyhelper.domain.schedule.model

data class LessonWindow(
    val previousLessonTime: LessonTime,
    val nextLessonTime: LessonTime
) : ScheduleItem