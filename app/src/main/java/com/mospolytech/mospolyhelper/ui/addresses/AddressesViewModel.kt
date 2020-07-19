package com.mospolytech.mospolyhelper.ui.addresses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.repository.addresses.AddressesDao
import com.mospolytech.mospolyhelper.repository.addresses.Addresses
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class AddressesViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val dao: AddressesDao
) : ViewModelBase(mediator, AddressesViewModel::class.java.simpleName) {
    var addresses: MutableLiveData<Addresses?> = MutableLiveData()
    val addressesType = MutableLiveData("")


    fun refresh() {
        viewModelScope.async(Dispatchers.IO) {
            val addresses = dao.getAddresses(true)
            withContext(Dispatchers.Main) {
                this@AddressesViewModel.addresses.value = addresses
            }
        }
    }

    fun setUpAddresses() {
        viewModelScope.async(Dispatchers.IO) {
            val addresses = dao.getAddresses(false)
            withContext(Dispatchers.Main) {
                this@AddressesViewModel.addresses.value = addresses
            }
        }
    }


}
