package com.mospolytech.domain.peoples.model

import com.mospolytech.domain.base.model.EducationType
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
