package com.mospolytech.mospolyhelper.domain.account.students.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: Int,
    val name: String,
    val group: String,
    val direction: String,
    val specialization: String,
    val course: String,
    val educationForm: String
)