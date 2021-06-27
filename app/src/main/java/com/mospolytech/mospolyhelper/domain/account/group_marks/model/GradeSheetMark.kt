package com.mospolytech.mospolyhelper.domain.account.group_marks.model

import kotlinx.serialization.Serializable

@Serializable
data class GradeSheetMark(
    val name: String,
    val mark: String
)
