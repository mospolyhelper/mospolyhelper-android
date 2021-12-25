package com.mospolytech.features.account.payments

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Application
import com.mospolytech.domain.account.model.Payments
import com.mospolytech.domain.account.repository.ApplicationsRepository
import com.mospolytech.domain.account.repository.PaymentsRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.launch

class PaymentsViewModel(private val repository: PaymentsRepository) :
    BaseViewModel<PaymentsState, PaymentsMutator, Nothing>(PaymentsState(), PaymentsMutator()) {

        init {
            viewModelScope.launch {
                repository.getPayments().execute(
                    onStart = {
                        mutateState {
                            setLoading(true)
                        }
                    },
                    onSuccess = {
                        mutateState {
                            setData(it)
                        }
                    },
                    onError = {
                        mutateState {
                            setError(true)
                        }
                    }
                )
            }
        }

}

data class PaymentsState(
    val data: List<Payments> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class PaymentsMutator : BaseMutator<PaymentsState>() {
    fun setData(data: List<Payments>) {
        state = state.copy(data = data, isLoading = false, isError = false)
    }

    fun setLoading(isLoading: Boolean) {
        state = state.copy(isLoading = isLoading, isError = !isLoading && state.isError)
    }

    fun setError(isError: Boolean) {
        state = state.copy(isError = isError, isLoading = false)
    }
}