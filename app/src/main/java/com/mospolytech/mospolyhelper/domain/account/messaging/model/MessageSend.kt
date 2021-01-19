package com.mospolytech.mospolyhelper.domain.account.messaging.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.*

@Serializable
data class MessageSend(
    val dialogKey: String,
    val message: String,
    val fileNames: List<String>?
)