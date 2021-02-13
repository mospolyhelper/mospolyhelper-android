package com.mospolytech.mospolyhelper.domain.schedule.model.tag

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val title: String,
    val color: Int
    )