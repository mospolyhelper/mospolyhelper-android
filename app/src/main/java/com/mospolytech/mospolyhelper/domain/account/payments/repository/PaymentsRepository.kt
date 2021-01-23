package com.mospolytech.mospolyhelper.domain.account.payments.repository

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    suspend fun getPayments(): Flow<Result<Payments>>
    suspend fun getLocalPayments(): Flow<Result<Payments>>
}