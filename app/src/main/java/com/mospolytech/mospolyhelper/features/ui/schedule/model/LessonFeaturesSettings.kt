package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.schedule.model.AuditoriumSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule

data class LessonFeaturesSettings(
    val showTeachers: Boolean,
    val showGroups: Boolean,
    val showAuditoriums: Boolean,
) {
    companion object {
        fun fromUserSchedule(user: UserSchedule?): LessonFeaturesSettings {
            if (user == null)
                return LessonFeaturesSettings(true, true, true)
            var showTeachers = true
            var showGroups = true
            var showAuditoriums = true
            when (user) {
                is TeacherSchedule -> showTeachers = false
                is StudentSchedule -> showGroups = false
                is AuditoriumSchedule -> showAuditoriums = false
            }
            return LessonFeaturesSettings(
                showTeachers,
                showGroups,
                showAuditoriums
            )
        }
    }
}