package com.mospolytech.domain.schedule.model.place

import java.time.LocalDateTime

data class PlaceFilters(
    val ids: List<String>? = null,
    val dateTimeFrom: LocalDateTime,
    val dateTimeTo: LocalDateTime
)