package com.mospolytech.mospolyhelper.domain.schedule.model.lesson

import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleItem

data class LessonWindow(
    val previousLessonTime: LessonTime,
    val nextLessonTime: LessonTime
) : ScheduleItem