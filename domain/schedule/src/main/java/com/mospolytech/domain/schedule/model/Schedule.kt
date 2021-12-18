package com.mospolytech.domain.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val lessons: List<ScheduleDay>
)