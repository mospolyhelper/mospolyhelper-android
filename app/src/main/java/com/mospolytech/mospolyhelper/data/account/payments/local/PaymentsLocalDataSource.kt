package com.mospolytech.mospolyhelper.data.account.payments.local

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result

class PaymentsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(payments: String): Result<Payments> {
        return try {
                Result.success(Json.decodeFromString<Payments>(payments))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(payments: Payments) {
        val currentInfo = Json.encodeToString(payments)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Payments, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Payments, "")
    }
}