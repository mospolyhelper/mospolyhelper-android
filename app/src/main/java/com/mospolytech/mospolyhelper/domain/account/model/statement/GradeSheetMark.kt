package com.mospolytech.mospolyhelper.domain.account.model.statement

import kotlinx.serialization.Serializable

@Serializable
data class GradeSheetMark(
    val name: String,
    val mark: String
)
