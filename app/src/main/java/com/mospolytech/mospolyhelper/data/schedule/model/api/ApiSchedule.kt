package com.mospolytech.mospolyhelper.data.schedule.model.api

import kotlinx.serialization.Serializable

@Serializable
class ApiSchedule(
    val status: String,
    val message: String? = null,
    val grid: Map<String, Map<String, List<ApiLesson>>>? = null,
    val group: String? = null,
    val isSession: Boolean? = null
)