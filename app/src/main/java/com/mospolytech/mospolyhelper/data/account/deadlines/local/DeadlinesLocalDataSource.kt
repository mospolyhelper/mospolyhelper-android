package com.mospolytech.mospolyhelper.data.account.deadlines.local

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result

class DeadlinesLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun get(deadlines: String): Result<List<Deadline>> {
        return try {
            val res = Json.decodeFromString<List<Deadline>>(deadlines)
            res.sortedBy {
                it.pinned
            }
            res.sortedBy {
                !it.completed
            }
            Result.success(res)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(deadlines: List<Deadline>) {
        deadlines.sortedBy {
            it.pinned
        }
        deadlines.sortedBy {
            !it.completed
        }
        val currentInfo = Json.encodeToString(deadlines)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Deadlines, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Deadlines, "")
    }
}