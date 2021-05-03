package com.mospolytech.mospolyhelper.domain.schedule.model.info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherInfo(
    @SerialName("name")
    val name: String,
    @SerialName("dep")
    val department: String
)