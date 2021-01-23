package com.mospolytech.mospolyhelper.domain.account.payments.usecase

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.payments.repository.PaymentsRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class PaymentsUseCase(
    private val repository: PaymentsRepository
) {
    suspend fun getInfo(): Flow<Result<Payments>> =
        repository.getPayments().onStart {
            emit(Result.loading())
        }
    suspend fun getLocalInfo(): Flow<Result<Payments>> =
        repository.getLocalPayments().onStart {
            //emit(Result.loading())
        }

}