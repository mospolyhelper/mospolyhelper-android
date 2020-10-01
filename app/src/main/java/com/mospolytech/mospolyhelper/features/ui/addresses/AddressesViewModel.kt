package com.mospolytech.mospolyhelper.features.ui.addresses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.data.addresses.repository.AddressesRepositoryImpl
import com.mospolytech.mospolyhelper.domain.addresses.model.Addresses
import com.mospolytech.mospolyhelper.domain.addresses.repository.AddressesRepository
import com.mospolytech.mospolyhelper.domain.addresses.usecase.AddressesUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class AddressesViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: AddressesUseCase
) : ViewModelBase(mediator, AddressesViewModel::class.java.simpleName) {
    var addresses = MutableStateFlow<Addresses?>(null)
    val addressesType = MutableStateFlow("")


    fun refresh() {
        viewModelScope.async(Dispatchers.IO) {
            useCase.getAddresses(true).collect {
                this@AddressesViewModel.addresses.value = it
            }
        }
    }

    fun setUpAddresses() {
        viewModelScope.async(Dispatchers.IO) {
            useCase.getAddresses(false).collect {
                this@AddressesViewModel.addresses.value = it
            }
        }
    }


}
