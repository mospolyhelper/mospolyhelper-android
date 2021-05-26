package com.mospolytech.mospolyhelper.features.ui.account.payments

import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.payments.usecase.PaymentsUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class PaymentsViewModel(mediator: Mediator<String, ViewModelMessage>,
                        private val useCase: PaymentsUseCase
): ViewModelBase(mediator, PaymentsViewModel::class.java.simpleName), KoinComponent {

    val payments = MutableStateFlow<Result2<Payments>>(Result2.loading())

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