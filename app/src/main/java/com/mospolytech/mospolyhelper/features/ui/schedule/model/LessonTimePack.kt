package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime

data class LessonTimePack(
    val time: LessonTime
) : ScheduleItemPacked