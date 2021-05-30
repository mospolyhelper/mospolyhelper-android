package com.mospolytech.mospolyhelper.features.ui.account.payments

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.payments.usecase.PaymentsUseCase
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class PaymentsViewModel(
    private val useCase: PaymentsUseCase,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val payments = MutableStateFlow<Result<Payments>>(Result.loading())
    val auth = MutableStateFlow<Result<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            payments.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            payments.value = it
        }
        useCase.getInfo().collect {
            payments.value = it
        }
    }
}