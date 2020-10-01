package com.mospolytech.mospolyhelper.domain.addresses.repository

import com.mospolytech.mospolyhelper.domain.addresses.model.Addresses
import kotlinx.coroutines.flow.Flow

interface AddressesRepository {
    fun getAddresses(refresh: Boolean): Flow<Addresses?>
}