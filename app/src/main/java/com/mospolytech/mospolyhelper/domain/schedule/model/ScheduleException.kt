package com.mospolytech.mospolyhelper.domain.schedule.model

sealed class ScheduleException : Exception() {
    object ScheduleNotFound : ScheduleException()
    object UserIsNull : ScheduleException()
}
