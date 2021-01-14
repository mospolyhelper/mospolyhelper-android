package com.mospolytech.mospolyhelper.domain.account.payments.model

data class Payments(
    val contracts: Map<String, Contract>
)