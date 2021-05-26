package com.mospolytech.mospolyhelper.domain.account.payments.usecase

import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.payments.repository.PaymentsRepository
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class PaymentsUseCase(
    private val repository: PaymentsRepository
) {
    suspend fun getInfo(): Flow<Result2<Payments>> =
        repository.getPayments().onStart {
            emit(Result2.loading())
        }
    suspend fun getLocalInfo(): Flow<Result2<Payments>> =
        repository.getLocalPayments().onStart {
            //emit(Result2.loading())
        }

}