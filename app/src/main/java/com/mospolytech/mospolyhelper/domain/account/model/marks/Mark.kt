package com.mospolytech.mospolyhelper.domain.account.model.marks

import kotlinx.serialization.Serializable

@Serializable
open class Mark(
    val subject: String,
    val loadType: String,
    val mark: String,
)