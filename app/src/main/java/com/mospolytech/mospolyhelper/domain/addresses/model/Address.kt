package com.mospolytech.mospolyhelper.domain.addresses.model

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val title: String,
    val description: String
)