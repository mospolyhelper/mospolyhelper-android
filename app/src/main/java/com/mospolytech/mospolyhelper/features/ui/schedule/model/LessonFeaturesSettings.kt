package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.model.AuditoriumScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource

data class LessonFeaturesSettings(
    val showTeachers: Boolean,
    val showGroups: Boolean,
    val showAuditoriums: Boolean,
) {
    companion object {
        fun fromUserSchedule(source: ScheduleSource): LessonFeaturesSettings {
            var showTeachers = true
            var showGroups = true
            var showAuditoriums = true
            when (source) {
                is TeacherScheduleSource -> showTeachers = false
                is StudentScheduleSource -> showGroups = false
                is AuditoriumScheduleSource -> showAuditoriums = false
            }
            return LessonFeaturesSettings(
                showTeachers,
                showGroups,
                showAuditoriums
            )
        }
    }
}