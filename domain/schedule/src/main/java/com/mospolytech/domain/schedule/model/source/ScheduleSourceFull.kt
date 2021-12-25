package com.mospolytech.domain.schedule.model.source

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleSourceFull(
    val type: ScheduleSources,
    val key: String,
    val title: String,
    val description: String,
    val avatarUrl: String
)