package com.mospolytech.mospolyhelper.domain.account.payments.model

import kotlinx.serialization.Serializable

@Serializable
data class Contract(
    val name: String,
    val paidAmount: Int,
    val debt: Int,
    val debtDate: String,
    val remainingAmount: Int,
    val expirationDate: String,
    val payments: List<Payment>,
    val sberQR: String
)