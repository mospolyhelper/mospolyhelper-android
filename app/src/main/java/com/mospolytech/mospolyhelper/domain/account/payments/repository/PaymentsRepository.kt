package com.mospolytech.mospolyhelper.domain.account.payments.repository

import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    suspend fun getPayments(): Flow<Result2<Payments>>
    suspend fun getLocalPayments(): Flow<Result2<Payments>>
}