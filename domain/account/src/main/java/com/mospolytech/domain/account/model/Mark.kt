package com.mospolytech.domain.perfomance.model

import com.mospolytech.domain.base.utils.converters.LocalDateTimeConverter
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Mark(
    val id: String,
    val name: String,
    val type: ExamType,
    val mark: String,
    @Serializable(LocalDateTimeConverter::class)
    val dateTime: LocalDateTime
)
