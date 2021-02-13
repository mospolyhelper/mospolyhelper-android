package com.mospolytech.mospolyhelper.domain.account.payments.model

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val date: String,
    val amount: Int
)