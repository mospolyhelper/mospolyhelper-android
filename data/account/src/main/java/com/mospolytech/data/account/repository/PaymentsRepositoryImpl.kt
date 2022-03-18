package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.domain.account.model.PaymentType
import com.mospolytech.domain.account.repository.PaymentsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class PaymentsRepositoryImpl(
    private val api: AccountService
): PaymentsRepository {
    override fun getPaymentTypes() =
        api.getPaymentsTypes()
            .flowOn(Dispatchers.IO)

    override fun getPayment(type: PaymentType) =
        api.getPayment(type.name.lowercase())
            .flowOn(Dispatchers.IO)

    override fun getPayments() =
        api.getPayments()
            .flowOn(Dispatchers.IO)
}