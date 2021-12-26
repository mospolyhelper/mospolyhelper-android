package com.mospolytech.domain.account.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PaymentType {
    @SerialName("dormitory")
    Dormitory,
    @SerialName("education")
    Education
}

fun PaymentType.print(): String = when (this) {
    PaymentType.Dormitory -> "Общежитие"
    PaymentType.Education -> "Образование"
}