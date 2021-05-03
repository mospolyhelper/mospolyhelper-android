package com.mospolytech.mospolyhelper.domain.schedule.model.info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuditoriumInfo(
    @SerialName("des")
    val description: String,
    @SerialName("norm")
    val normalized: String
)