package com.mospolytech.mospolyhelper.domain.account.payments.model

import kotlinx.serialization.Serializable

@Serializable
data class Payments(
    val contracts: Map<String, Contract>
)