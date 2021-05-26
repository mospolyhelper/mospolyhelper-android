package com.mospolytech.mospolyhelper.data.account.payments.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PaymentsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(payments: String): Result2<Payments> {
        return try {
                Result2.success(Json.decodeFromString<Payments>(payments))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }

    fun set(payments: Payments) {
        val currentInfo = Json.encodeToString(payments)
        if (getJson() != currentInfo)
            prefDataSource.set(PreferenceKeys.Payments, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.get(PreferenceKeys.Payments, "")
    }
}