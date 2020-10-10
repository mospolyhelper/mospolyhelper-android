package com.mospolytech.mospolyhelper.domain.addresses.repository

import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import kotlinx.coroutines.flow.Flow

interface AddressesRepository {
    fun getAddresses(refresh: Boolean): Flow<AddressMap?>
}