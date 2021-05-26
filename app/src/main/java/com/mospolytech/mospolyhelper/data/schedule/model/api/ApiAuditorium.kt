package com.mospolytech.mospolyhelper.data.schedule.model.api

import kotlinx.serialization.Serializable

@Serializable
class ApiAuditorium(
    val title: String,
    val type: String,
    val url: String,
    val color: String
)