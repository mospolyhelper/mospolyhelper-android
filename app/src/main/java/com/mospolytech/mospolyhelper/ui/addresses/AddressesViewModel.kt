package com.mospolytech.mospolyhelper.ui.addresses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.repository.dao.AddressesDao
import com.mospolytech.mospolyhelper.repository.models.Addresses
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressesViewModel :
    ViewModelBase(Mediator(), AddressesViewModel::class.java.simpleName) {
    var addresses: MutableLiveData<Addresses?> = MutableLiveData<Addresses?>()
    val dao = AddressesDao()


    fun refresh() {
        viewModelScope.launch {
            addresses.value =
                withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                    dao.getAddresses(true)
                }
        }
    }

    fun setUpAddresses() {
        viewModelScope.launch {
            addresses.value =
                withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                    dao.getAddresses(false)
                }
        }
    }


}
