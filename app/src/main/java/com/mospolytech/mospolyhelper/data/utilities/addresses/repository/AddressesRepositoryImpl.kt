package com.mospolytech.mospolyhelper.data.utilities.addresses.repository

import com.mospolytech.mospolyhelper.data.core.local.AssetsDataSource
import com.mospolytech.mospolyhelper.data.utilities.addresses.local.AddressesLocalStorageDataSource
import com.mospolytech.mospolyhelper.data.utilities.addresses.remote.AddressesRemoteDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import com.mospolytech.mospolyhelper.domain.addresses.repository.AddressesRepository
import kotlinx.coroutines.flow.flow

class AddressesRepositoryImpl(
    private val remoteDataSource: AddressesRemoteDataSource,
    private val localStorageDataSource: AddressesLocalStorageDataSource,
    private val assetsDataSource: AssetsDataSource
): AddressesRepository {

    override fun getAddresses(refresh: Boolean) = flow<AddressMap?> {
        emit(get(refresh) ?: get(!refresh))
    }

    private fun get(refresh: Boolean): AddressMap? {
        val addressMap: AddressMap?
        if (refresh) {
            addressMap = remoteDataSource.get()
            if (addressMap != null) {
                localStorageDataSource.set(addressMap)
            }
        } else {
            addressMap = localStorageDataSource.get() ?: assetsDataSource.getFromJson("addresses.json")
        }
        return addressMap
    }
}