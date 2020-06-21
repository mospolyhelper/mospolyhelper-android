package com.mospolytech.mospolyhelper.ui.addresses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.repository.dao.AddressesDao
import com.mospolytech.mospolyhelper.repository.models.Addresses
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.utils.StaticDI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressesViewModel :
    ViewModelBase(StaticDI.viewModelMediator, AddressesViewModel::class.java.simpleName) {
    var addresses: MutableLiveData<Addresses?> = MutableLiveData<Addresses?>()
    private val dao = AddressesDao()
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
