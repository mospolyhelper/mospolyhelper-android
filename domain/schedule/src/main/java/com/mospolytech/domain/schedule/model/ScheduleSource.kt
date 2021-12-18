package com.mospolytech.domain.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleSource(
    val type: ScheduleSources,
    val key: String
)

@Serializable
data class ScheduleSourceFull(
    val type: ScheduleSources,
    val key: String,
    val title: String,
    val description: String,
    val avatarUrl: String
)