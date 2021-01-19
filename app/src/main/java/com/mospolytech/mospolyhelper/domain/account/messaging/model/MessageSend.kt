package com.mospolytech.mospolyhelper.domain.account.messaging.model

data class MessageSend(
    val dialogKey: String,
    val message: String,
    val fileNames: List<String>?
)