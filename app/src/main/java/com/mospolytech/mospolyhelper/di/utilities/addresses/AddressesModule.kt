package com.mospolytech.mospolyhelper.di.utilities.addresses

import com.mospolytech.mospolyhelper.data.utilities.addresses.local.AddressesLocalStorageDataSource
import com.mospolytech.mospolyhelper.data.utilities.addresses.remote.AddressesRemoteDataSource
import com.mospolytech.mospolyhelper.data.utilities.addresses.repository.AddressesRepositoryImpl
import com.mospolytech.mospolyhelper.domain.addresses.repository.AddressesRepository
import com.mospolytech.mospolyhelper.domain.addresses.usecase.AddressesUseCase
import com.mospolytech.mospolyhelper.features.ui.utilities.addresses.AddressesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addressesModule = module {
    single { AddressesRemoteDataSource() }
    single { AddressesLocalStorageDataSource(get()) }

    single<AddressesRepository> { AddressesRepositoryImpl(get(), get(), get()) }

    single { AddressesUseCase(get()) }

    viewModel<AddressesViewModel> { AddressesViewModel(get(), get()) }
}