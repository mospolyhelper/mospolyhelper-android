package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonDateFilter

data class ScheduleSettings(
    val showEmptyLessons: Boolean,
    val dateFilter: LessonDateFilter,
    val lessonFeatures: LessonFeaturesSettings
) {
    companion object {
        val Default = ScheduleSettings(
            false,
            LessonDateFilter.Default,
            LessonFeaturesSettings.All
        )
    }
}