package com.mospolytech.mospolyhelper.domain.account.info.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class Info(
    val name: String,
    val status: String,
    val sex: String,
    val birthDate: String,
    val studentCode: String,
    val faculty: String,
    val course: String,
    val group: String,
    val dormitory: String,
    val dormitoryRoom: String,
    val direction: String,
    val specialization: String,
    val educationPeriod: String,
    val educationForm: String,
    val financingType: String,
    val educationLevel: String,
    val admissionYear: String,
    val orders: List<String>
)