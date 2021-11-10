package com.mospolytech.mospolyhelper.domain.account.model.dialog

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val url: String,
    val name: String
)