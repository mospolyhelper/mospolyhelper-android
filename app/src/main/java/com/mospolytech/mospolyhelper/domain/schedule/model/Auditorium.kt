package com.mospolytech.mospolyhelper.domain.schedule.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Auditorium(
    val title: String,
    val type: String,
    val color: String,
    val url: String
    ) : Parcelable