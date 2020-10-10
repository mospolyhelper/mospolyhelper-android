package com.mospolytech.mospolyhelper.domain.addresses.model

data class AddressMap(
    val addresses: Map<String, List<Address>>,
    val version: Int
): Map<String, List<Address>> by addresses