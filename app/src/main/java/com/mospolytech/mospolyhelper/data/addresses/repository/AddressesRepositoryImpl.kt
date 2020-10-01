package com.mospolytech.mospolyhelper.data.addresses.repository

import com.mospolytech.mospolyhelper.data.addresses.local.AddressesLocalAssetsDataSource
import com.mospolytech.mospolyhelper.data.addresses.local.AddressesLocalStorageDataSource
import com.mospolytech.mospolyhelper.data.addresses.remote.AddressesRemoteDataSource
import com.mospolytech.mospolyhelper.domain.addresses.model.Addresses
import com.mospolytech.mospolyhelper.domain.addresses.repository.AddressesRepository
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class AddressesRepositoryImpl(
    private val remoteDataSource: AddressesRemoteDataSource,
    private val localStorageDataSource: AddressesLocalStorageDataSource,
    private val localAssetsDataSource: AddressesLocalAssetsDataSource
): AddressesRepository {

    override fun getAddresses(refresh: Boolean) = flow<Addresses?> {
        emit(get(refresh) ?: get(!refresh))
    }

    private fun get(refresh: Boolean): Addresses? {
        val addresses: Addresses?
        if (refresh) {
            addresses = remoteDataSource.get()
            if (addresses != null) {
                localStorageDataSource.set(addresses)
            }
        } else {
            addresses = localStorageDataSource.get() ?: localAssetsDataSource.get()
        }
        return addresses
    }
}