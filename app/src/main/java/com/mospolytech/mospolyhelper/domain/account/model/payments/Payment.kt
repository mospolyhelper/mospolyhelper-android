package com.mospolytech.mospolyhelper.domain.account.model.payments

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val date: String,
    val amount: Int
)