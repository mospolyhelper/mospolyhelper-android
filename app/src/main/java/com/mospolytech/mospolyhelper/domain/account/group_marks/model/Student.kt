package com.mospolytech.mospolyhelper.domain.account.group_marks.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val name: String,
    val mark: String,
    val ticket: String,
    val recordBook: String,
    val blocked: Boolean
)
