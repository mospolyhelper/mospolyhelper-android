package com.mospolytech.domain.payments.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PaymentType {
    @SerialName("dormitory")
    Dormitory,
    @SerialName("education")
    Education
}