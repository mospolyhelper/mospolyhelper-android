package com.mospolytech.mospolyhelper.domain.addresses.usecase

import com.mospolytech.mospolyhelper.domain.addresses.model.Addresses
import com.mospolytech.mospolyhelper.domain.addresses.repository.AddressesRepository
import kotlinx.coroutines.flow.flow

class AddressesUseCase(
    private val repository: AddressesRepository
) {
    fun getAddresses(refresh: Boolean) = repository.getAddresses(refresh)
}