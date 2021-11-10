package com.mospolytech.mospolyhelper.domain.account.model.group_marks

import kotlinx.serialization.Serializable

@Serializable
class Teacher(
    val uid: String,
    val name: String,
    val signed: Boolean
)