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