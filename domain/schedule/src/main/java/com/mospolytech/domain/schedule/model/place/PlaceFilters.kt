package com.mospolytech.domain.schedule.model.place

import com.mospolytech.domain.base.utils.converters.LocalDateTimeConverter
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class PlaceFilters(
    val ids: List<String>? = null,
    @Serializable(with = LocalDateTimeConverter::class)
    val dateTimeFrom: LocalDateTime,
    @Serializable(with = LocalDateTimeConverter::class)
    val dateTimeTo: LocalDateTime
)