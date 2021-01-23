package com.mospolytech.mospolyhelper.domain.account.applications.model

data class Application(
    val registrationNumber: String,
    val name: String,
    val dateTime: String,
    val status: String,
    val department: String,
    val note: String,
    val info: String,
    var isShown: Boolean = false
)