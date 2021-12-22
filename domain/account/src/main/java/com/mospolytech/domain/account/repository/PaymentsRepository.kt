package com.mospolytech.domain.account.repository

import com.mospolytech.domain.account.model.PaymentType
import com.mospolytech.domain.account.model.Payments

interface PaymentsRepository {
    fun getPaymentTypes(): List<PaymentType>
    fun getPayment(type: PaymentType): Payments
    fun getPayments(): List<Payments>
}