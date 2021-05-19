package com.mospolytech.mospolyhelper.data.account.deadlines.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DeadlinesLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun get(deadlines: String): Result<List<Deadline>> {
        return try {
            var res = Json.decodeFromString<List<Deadline>>(deadlines)
            res = res.sortedBy {
                it.pinned
            }
            res = res.sortedBy {
                !it.completed
            }
            Result.success(res)

        } catch (e: Exception) {
            Result.success(emptyList())
        }
    }

    fun set(deadlines: List<Deadline>) {
        var list = deadlines.sortedBy {
            it.pinned
        }
        list = list.sortedBy {
            !it.completed
        }
        val currentInfo = Json.encodeToString(list)
        if (getJson() != currentInfo)
            prefDataSource.set(PreferenceKeys.Deadlines, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.get(PreferenceKeys.Deadlines, "")
    }
}