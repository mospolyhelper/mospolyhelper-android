package com.mospolytech.mospolyhelper.domain.account.model.marks

import kotlinx.serialization.Serializable

@Serializable
data class Marks(
    val marks: Map<String, Map<String, List<Mark>>>
)