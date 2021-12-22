package com.mospolytech.domain.account.repository

import com.mospolytech.domain.account.model.PaymentType
import com.mospolytech.domain.account.model.Payments
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
    fun getPaymentTypes(): Flow<Result<List<PaymentType>>>
    fun getPayment(type: PaymentType): Flow<Result<Payments>>
    fun getPayments(): Flow<Result<List<Payments>>>
}