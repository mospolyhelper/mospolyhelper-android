package com.mospolytech.mospolyhelper.domain.account.payments.usecase

import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.payments.repository.PaymentsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class PaymentsUseCase(
    private val repository: PaymentsRepository
) {
    suspend fun getInfo(): Flow<Result0<Payments>> =
        repository.getPayments().onStart {
            emit(Result0.Loading)
        }
    suspend fun getLocalInfo(): Flow<Result0<Payments>> =
        repository.getLocalPayments()

}