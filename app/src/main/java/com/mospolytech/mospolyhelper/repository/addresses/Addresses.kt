package com.mospolytech.mospolyhelper.repository.addresses

import java.util.*

data class Addresses(
    val addresses: Map<String, List<String>>,
    val version: Int
): Map<String, List<String>> by addresses