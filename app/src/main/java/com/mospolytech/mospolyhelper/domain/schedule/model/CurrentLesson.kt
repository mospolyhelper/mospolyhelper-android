package com.mospolytech.mospolyhelper.domain.schedule.model

data class CurrentLesson(
    val time: LessonTime,
    val isStarted: Boolean,
) {
    override fun equals(other: Any?) = false
    override fun hashCode(): Int = time.hashCode()
}