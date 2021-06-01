package com.mospolytech.mospolyhelper.domain.schedule.model

data class LessonFeaturesSettings(
    val showTeachers: Boolean,
    val showGroups: Boolean,
    val showAuditoriums: Boolean,
) {
    companion object {
        fun fromUserSchedule(userSchedule: UserSchedule): LessonFeaturesSettings {
            var showTeachers = true
            var showGroups = true
            var showAuditoriums = true
            when (userSchedule) {
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