package com.mospolytech.mospolyhelper.features.ui.account.payments

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.payments.Payments
import com.mospolytech.mospolyhelper.domain.account.repository.PaymentsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class PaymentsViewModel(
    private val repository: PaymentsRepository,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val payments = MutableStateFlow<Result0<Payments>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo() {
        repository.getPayments(emitLocal = false).collect {
            payments.value = it
        }
    }

    suspend fun getInfo() {
        repository.getPayments(emitLocal = true).collect {
            payments.value = it
        }
    }
}