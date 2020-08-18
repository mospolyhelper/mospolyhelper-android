package com.mospolytech.mospolyhelper.domain.addresses.model

data class Addresses(
    val addresses: Map<String, List<String>>,
    val version: Int
): Map<String, List<String>> by addresses