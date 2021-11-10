package com.mospolytech.mospolyhelper.domain.account.model.group_marks

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val name: String,
    val mark: String,
    val ticket: String,
    val recordBook: String,
    val blocked: Boolean
)
