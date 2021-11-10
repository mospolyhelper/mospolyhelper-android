package com.mospolytech.mospolyhelper.domain.account.model.statements

import kotlinx.serialization.Serializable

@Serializable
data class Statements(
    val semester: String,
    val semesterList: List<String>,
    val sheets: List<Statement>
)