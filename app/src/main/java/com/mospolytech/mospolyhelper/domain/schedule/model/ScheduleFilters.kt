package com.mospolytech.mospolyhelper.domain.schedule.model

import kotlinx.serialization.Serializable

@Serializable
class ScheduleFilters(
    val titles: Set<String>,
    val types: Set<String>,
    val teachers: Set<String>,
    val groups: Set<String>,
    val auditoriums: Set<String>,
)