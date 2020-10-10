package com.mospolytech.mospolyhelper.domain.addresses.usecase

import com.mospolytech.mospolyhelper.domain.addresses.repository.AddressesRepository

class AddressesUseCase(
    private val repository: AddressesRepository
) {
    fun getAddresses(refresh: Boolean) = repository.getAddresses(refresh)
}