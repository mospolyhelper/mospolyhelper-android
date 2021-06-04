package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonDateFilter
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag

data class LessonPack(
    val lesson: Lesson,
    val lessonTime: LessonTime,
    val tags: List<LessonTag>,
    val deadlines: List<Deadline>,
    val dateFilter: LessonDateFilter,
    val featuresSettings: LessonFeaturesSettings,
    val isEnabled: Boolean
) : ScheduleItemPacked