package com.mospolytech.mospolyhelper.repository.model

import java.util.*

data class Addresses(
    val addresses: SortedMap<String, List<String>>,
    val version: Int
): Map<String, List<String>> by addresses