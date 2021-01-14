package com.mospolytech.mospolyhelper.domain.account.students.model

data class Student(
    val id: Int,
    val name: String,
    val group: String,
    val direction: String,
    val specialization: String,
    val course: String,
    val educationForm: String
)