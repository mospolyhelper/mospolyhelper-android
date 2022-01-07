package com.mospolytech.features.account.personal

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Application
import com.mospolytech.domain.account.model.Order
import com.mospolytech.domain.account.model.Personal
import com.mospolytech.domain.account.repository.ApplicationsRepository
import com.mospolytech.domain.account.repository.PersonalRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.launch

class PersonalViewModel(private val repository: PersonalRepository) :
    BaseViewModel<PersonalState, PersonalMutator, Nothing>(PersonalState(), ::PersonalMutator) {

        init {
            load()
        }

    fun load() {
        viewModelScope.launch {
            repository.getPersonalInfo().execute(
                onStart = {
                    mutateState {
                        setPersonalLoading(true)
                    }
                },
                onSuccess = {
                    mutateState {
                        setPersonalData(it)
                    }
                },
                onError = {
                    mutateState {
                        setPersonalError(true)
                    }
                }
            )
        }
        viewModelScope.launch {
            repository.getOrders().execute(
                onStart = {
                    mutateState {
                        setOrdersLoading(true)
                    }
                },
                onSuccess = {
                    mutateState {
                        setOrdersData(it)
                    }
                },
                onError = {
                    mutateState {
                        setOrdersError(true)
                    }
                }
            )
        }
    }

}

data class PersonalState(
    val personal: Personal? = null,
    val orders: List<Order> = emptyList(),
    val isPersonalLoading: Boolean = false,
    val isPersonalError: Boolean = false,
    val isOrdersLoading: Boolean = false,
    val isOrdersError: Boolean = false
)

class PersonalMutator : BaseMutator<PersonalState>() {
    fun setOrdersData(data: List<Order>) {
        state = state.copy(orders = data, isOrdersLoading = false, isOrdersError = false)
    }

    fun setOrdersLoading(isLoading: Boolean) {
        state = state.copy(isOrdersLoading = isLoading, isOrdersError = !isLoading && state.isOrdersError)
    }

    fun setOrdersError(isError: Boolean) {
        state = state.copy(isOrdersError = isError, isOrdersLoading = false)
    }

    fun setPersonalData(data: Personal) {
        state = state.copy(personal = data, isPersonalLoading = false, isOrdersError = false)
    }

    fun setPersonalLoading(isLoading: Boolean) {
        state = state.copy(isPersonalLoading = isLoading, isOrdersError = !isLoading && state.isOrdersError)
    }

    fun setPersonalError(isError: Boolean) {
        state = state.copy(isOrdersError = isError, isPersonalLoading = false)
    }
}