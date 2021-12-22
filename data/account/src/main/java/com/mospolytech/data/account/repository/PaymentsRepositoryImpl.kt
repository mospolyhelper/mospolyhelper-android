package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.domain.account.model.PaymentType
import com.mospolytech.domain.account.model.Payments
import com.mospolytech.domain.account.repository.PaymentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PaymentsRepositoryImpl(private val api: AccountService): PaymentsRepository {
    override fun getPaymentTypes(): Flow<Result<List<PaymentType>>>  = flow {
        emit(api.getPaymentsTypes().toResult())
    }

    override fun getPayment(type: PaymentType): Flow<Result<Payments>> = flow {
        emit(api.getPayment(type.name.lowercase()).toResult())
    }

    override fun getPayments(): Flow<Result<List<Payments>>> = flow {
        emit(api.getPayments().toResult())
    }

}