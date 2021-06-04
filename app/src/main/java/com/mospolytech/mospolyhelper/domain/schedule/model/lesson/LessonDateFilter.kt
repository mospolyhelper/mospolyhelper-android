package com.mospolytech.mospolyhelper.domain.schedule.model.lesson

data class LessonDateFilter(
    val showEndedLessons: Boolean,
    val showCurrentLessons: Boolean,
    val showNotStartedLessons: Boolean,
) {
    companion object {
        val Default = LessonDateFilter(
            false,
            true,
            false
        )
    }
}