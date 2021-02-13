package com.mospolytech.mospolyhelper.data.account.statements.local

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result

class StatementsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(statements: String): Result<Statements> {
        return try {
            Result.success(Json.decodeFromString<Statements>(statements))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(statements: Statements) {
        val currentInfo = Json.encodeToString(statements)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Statements, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Statements, "")
    }
}