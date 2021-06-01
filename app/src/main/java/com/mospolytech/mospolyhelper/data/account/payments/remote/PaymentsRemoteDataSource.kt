package com.mospolytech.mospolyhelper.data.account.payments.remote

import com.mospolytech.mospolyhelper.data.account.payments.api.PaymentsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PaymentsRemoteDataSource(
    private val client: PaymentsHerokuClient
) {
    suspend fun get(sessionId: String): Result2<Payments> {
        return try {
            val res = client.getPayments(sessionId)
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }
}