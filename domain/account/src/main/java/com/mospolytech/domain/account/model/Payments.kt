package com.mospolytech.domain.payments.model

import com.mospolytech.domain.base.utils.converters.LocalDateConverter
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Payments(
    val id: String?,
    @Serializable(with = LocalDateConverter::class)
    val date: LocalDate?,
    val sum: Float?,
    val credit: Float?,
    @Serializable(with = LocalDateConverter::class)
    val dateCredit: LocalDate?,
    val unit: String,
    val paymentQR: String?,
    val payments: List<Payment>,
    val type: PaymentType?,
)