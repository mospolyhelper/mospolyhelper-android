package com.mospolytech.mospolyhelper.data.account.payments.remote

import com.mospolytech.mospolyhelper.data.account.payments.api.PaymentsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PaymentsRemoteDataSource(
    private val client: PaymentsHerokuClient
) {
    suspend fun get(sessionId: String): Result0<Payments> {
        return try {
            val res = client.getPayments(sessionId)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}