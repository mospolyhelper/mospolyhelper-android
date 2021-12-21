package com.mospolytech.domain.perfomance.model

import kotlinx.serialization.Serializable

@Serializable
data class Marks(
    val course: Int,
    val semester: Int,
    val marks: List<Mark>
)