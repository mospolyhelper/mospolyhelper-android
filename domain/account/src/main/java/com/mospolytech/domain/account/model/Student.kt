package com.mospolytech.domain.account.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: String,
    val name: String,
    val avatar: String?,
    val educationType: EducationType?,
    val course: Int?,
    val group: String?,
    val direction: String?,
    val specialization: String?,
    val educationForm: EducationForm?,
    val dialogId: String?,
    val additionalInfo: String?
)
