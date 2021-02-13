package com.mospolytech.mospolyhelper.domain.account.marks.model

import kotlinx.serialization.Serializable

@Serializable
data class Marks(
    val marks: Map<String, Map<String, List<Mark>>>
)