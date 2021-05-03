package com.mospolytech.mospolyhelper.domain.schedule.model.info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupInfo(
    @SerialName("code")
    val directionCode: String,
    @SerialName("dir")
    val direction: String,
    @SerialName("spec")
    val specialization: String,
    @SerialName("course")
    val course: Int,
    @SerialName("form")
    val educationForm: String,
    @SerialName("count")
    val count: Int
)