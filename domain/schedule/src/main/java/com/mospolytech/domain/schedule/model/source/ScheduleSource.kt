package com.mospolytech.domain.schedule.model.source

import com.mospolytech.domain.schedule.model.source.ScheduleSources
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleSource(
    val type: ScheduleSources,
    val key: String
)