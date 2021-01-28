package com.mospolytech.mospolyhelper.domain.account.statements.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Statements(
    val semester: String,
    val semesterList: List<String>,
    val sheets: List<Statement>
): Parcelable