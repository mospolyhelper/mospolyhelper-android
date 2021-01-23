package com.mospolytech.mospolyhelper.features.ui.account.payments

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.payments.usecase.PaymentsUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent

class PaymentsViewModel(mediator: Mediator<String, ViewModelMessage>,
                        private val useCase: PaymentsUseCase
): ViewModelBase(mediator, PaymentsViewModel::class.java.simpleName), KoinComponent {

    val payments = MutableStateFlow<Result<Payments>>(Result.loading())

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