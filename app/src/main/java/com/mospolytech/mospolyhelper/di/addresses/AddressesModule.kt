package com.mospolytech.mospolyhelper.di.addresses

import com.mospolytech.mospolyhelper.data.addresses.AddressesDao
import com.mospolytech.mospolyhelper.features.ui.addresses.AddressesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val addressesModule = module {
    single<AddressesDao> { AddressesDao() }

    viewModel<AddressesViewModel> { AddressesViewModel(get(), get()) }
}