package com.mospolytech.mospolyhelper.domain.account.repository

import com.mospolytech.mospolyhelper.domain.account.model.payments.Payments
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    suspend fun getPayments(emitLocal: Boolean = true): Flow<Result0<Payments>>
}