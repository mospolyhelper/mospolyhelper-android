package com.mospolytech.mospolyhelper.domain.account.model.payments

import kotlinx.serialization.Serializable

@Serializable
data class Payments(
    val contracts: Map<String, Contract>
)