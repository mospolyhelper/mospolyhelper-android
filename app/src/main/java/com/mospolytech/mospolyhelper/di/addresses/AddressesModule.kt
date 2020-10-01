package com.mospolytech.mospolyhelper.di.addresses

import com.mospolytech.mospolyhelper.data.addresses.local.AddressesLocalAssetsDataSource
import com.mospolytech.mospolyhelper.data.addresses.local.AddressesLocalStorageDataSource
import com.mospolytech.mospolyhelper.data.addresses.remote.AddressesRemoteDataSource
import com.mospolytech.mospolyhelper.data.addresses.repository.AddressesRepositoryImpl
import com.mospolytech.mospolyhelper.domain.addresses.repository.AddressesRepository
import com.mospolytech.mospolyhelper.domain.addresses.usecase.AddressesUseCase
import com.mospolytech.mospolyhelper.features.ui.addresses.AddressesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addressesModule = module {
    single { AddressesRemoteDataSource() }
    single { AddressesLocalStorageDataSource() }
    single { AddressesLocalAssetsDataSource() }

    single<AddressesRepository> { AddressesRepositoryImpl(get(), get(), get()) }

    single { AddressesUseCase(get()) }

    viewModel<AddressesViewModel> { AddressesViewModel(get(), get()) }
}