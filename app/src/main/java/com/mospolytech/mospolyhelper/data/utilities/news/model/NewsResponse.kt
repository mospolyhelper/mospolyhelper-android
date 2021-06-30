package com.mospolytech.mospolyhelper.data.utilities.news.model

import kotlinx.serialization.Serializable

@Serializable
class NewsResponse(
    val html: String,
    val title: Boolean,
    val h1: String,
    val url: String
)