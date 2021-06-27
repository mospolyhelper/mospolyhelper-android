package com.mospolytech.mospolyhelper.domain.account.group_marks.model

import kotlinx.serialization.Serializable

@Serializable
class Teacher(
    val uid: String,
    val name: String,
    val signed: Boolean
)