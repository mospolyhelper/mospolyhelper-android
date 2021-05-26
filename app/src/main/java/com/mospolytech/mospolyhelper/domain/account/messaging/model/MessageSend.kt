package com.mospolytech.mospolyhelper.domain.account.messaging.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageSend(
    val dialogKey: String,
    val message: String,
    val fileNames: List<String>?
)