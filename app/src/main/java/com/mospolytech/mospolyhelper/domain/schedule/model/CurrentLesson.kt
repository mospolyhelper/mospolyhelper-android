package com.mospolytech.mospolyhelper.domain.schedule.model

data class CurrentLesson(
    val order: Int,
    val isEvening: Boolean,
    val isStarted: Boolean,
) {
    override fun equals(other: Any?) = false
    override fun hashCode(): Int {
        var result = order
        result = 31 * result + isStarted.hashCode()
        result = 31 * result + isEvening.hashCode()
        return result
    }
}