package com.mospolytech.mospolyhelper.domain.account.model.teachers

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: Int,
    val name: String,
    val info: String,
    val avatarUrl: String,
    val status: String,
    val dialogKey: String
)