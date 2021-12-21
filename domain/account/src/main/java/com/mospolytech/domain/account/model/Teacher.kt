package com.mospolytech.domain.account.model

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: String,
    val name: String,
    val avatar: String?,
    val dialogId: String?,
    val position: String?,
    val department: String?,
    val additionalInfo: String?
)
