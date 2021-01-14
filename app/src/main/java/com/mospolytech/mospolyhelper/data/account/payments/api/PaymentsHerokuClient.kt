package com.mospolytech.mospolyhelper.data.account.payments.api

import io.ktor.client.*
import io.ktor.client.request.*

class PaymentsHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_PAYMENTS = "$BASE_URL$ACCOUNT_MODULE/payments"
    }

    suspend fun getPayments(sessionId: String): String {
        return client.get(GET_PAYMENTS) {
            header("sessionId", sessionId)
        }
    }
}