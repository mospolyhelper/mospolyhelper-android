package com.mospolytech.data.base.local

import com.mospolytech.data.base.model.PreferenceDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface PreferencesDS {
    fun setJson(str: String, key: String)
    fun getJson(key: String): Result<PreferenceDao?>
    fun flowOfPreferences(key: String): Flow<PreferenceDao?>
}

inline fun <reified T> PreferencesDS.set(obj: T, key: String): Result<Unit> {
    return kotlin.runCatching {
        val str = Json.encodeToString(obj)
        setJson(str, key)
    }
}

inline fun <reified T> PreferencesDS.get(key: String): Result<T?> {
    val json = getJson(key)
    return json.mapCatching { it?.let { Json.decodeFromString(it.value) } }
}

inline fun <reified T> PreferencesDS.flowOf(key: String): Flow<Result<T?>> {
    return flowOfPreferences(key)
        .map { kotlin.runCatching { it?.let { Json.decodeFromString(it.value) } } }
}

inline fun <reified T> PreferencesDS.get(key: String, defaultValue: T): T {
    return get<T>(key).getOrNull() ?: defaultValue
}