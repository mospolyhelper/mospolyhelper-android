package com.mospolytech.domain.personal.model

import com.mospolytech.domain.base.utils.converters.LocalDateConverter
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Order(
    val number: String,
    @Serializable(LocalDateConverter::class)
    val date: LocalDate,
    val additionalInfo: String?
)
