package com.mospolytech.mospolyhelper.data.utils

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.core.repository.PreferencesRepository
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> SharedPreferencesDataSource.getObject(key: String = T::class.java.name): T? {
    return try {
        val json = get(key, "")
        if (json.isEmpty()) {
            null
        } else {
            Json.decodeFromString<T>(json)
        }
    } catch (e: Exception) {
        null
    }
}

@Deprecated("use setObject(key, value)", ReplaceWith("setObject(value, key)"))
inline fun <reified T> SharedPreferencesDataSource.setObject(key: String = T::class.java.name, value: T) {
    this.setObject(value, key)
}

inline fun <reified T> SharedPreferencesDataSource.setResultObject(value: Result0<T>, key: String = T::class.java.name) {
    value.onSuccess {
        this.setObject(it, key)
    }
}

inline fun <reified T> SharedPreferencesDataSource.clearObject(key: String = T::class.java.name) {
    this.set(key, "")
}

inline fun <reified T> SharedPreferencesDataSource.getResultObject(key: String = T::class.java.name): Result0<T>? {
    return this.getObject<T>(key)?.let {
        Result0.Success(it)
    }
}

inline fun <reified T> SharedPreferencesDataSource.setObject(value: T, key: String = T::class.java.name) {
    try {
        if (value == null) {
            set(key, "")
        } else {
            set(key, Json.encodeToString(value))
        }
    } catch (e: Exception) {
        set(key, "")
    }
}

inline fun <reified T> PreferencesRepository.getFromJson(key: String): T? {
    return try {
        val json = get(key, "")
        if (json.isEmpty()) {
            null
        } else {
            Json.decodeFromString<T>(json)
        }
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> PreferencesRepository.setAsJson(key: String, value: T) {
    try {
        if (value == null) {
            set(key, "")
        } else {
            set(key, Json.encodeToString(value))
        }
    } catch (e: Exception) {
        set(key, "")
    }
}