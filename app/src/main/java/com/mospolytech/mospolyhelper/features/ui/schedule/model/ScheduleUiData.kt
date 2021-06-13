package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag

data class ScheduleUiData(
    val schedule: Schedule,
    val tags: List<LessonTag>,
    val deadlines: Map<String, List<Deadline>>,
    val settings: ScheduleSettings
)