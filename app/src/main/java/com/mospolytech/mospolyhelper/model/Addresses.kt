package com.mospolytech.mospolyhelper.model

import java.util.*

data class Addresses(
    val addresses: SortedMap<String, List<String>>,
    val version: Int
)