package com.mospolytech.mospolyhelper.domain.account.deadlines.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Deadline(
    val id: Int,
    val name: String,
    val description: String,
    val pinned: Boolean,
    val date: String,
    val completed: Boolean,
    val importance: Int
): Parcelable