package com.mospolytech.mospolyhelper.data.account.payments.remote

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.account.payments.api.PaymentsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.utils.Result

class PaymentsRemoteDataSource(
    private val client: PaymentsHerokuClient
) {
    suspend fun get(sessionId: String): Result<Payments> {
        return try {
            val res = client.getPayments(sessionId)
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}