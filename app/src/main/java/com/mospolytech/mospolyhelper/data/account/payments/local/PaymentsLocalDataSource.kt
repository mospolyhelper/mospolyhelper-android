package com.mospolytech.mospolyhelper.data.account.payments.local

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class PaymentsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(payments: String): Result<Payments> {
        return try {
                Result.success(Klaxon().parse<Payments>(payments)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(payments: Payments) {
        val currentInfo = Klaxon().toJsonString(payments)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Payments, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Payments, "")
    }
}