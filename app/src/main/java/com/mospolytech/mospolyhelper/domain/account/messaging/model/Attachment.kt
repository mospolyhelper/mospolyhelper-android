package com.mospolytech.mospolyhelper.domain.account.messaging.model

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val url: String,
    val name: String
)