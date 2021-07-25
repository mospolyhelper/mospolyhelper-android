package com.mospolytech.mospolyhelper.data.utils

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.core.repository.PreferencesRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> SharedPreferencesDataSource.getFromJson(key: String = T::class.java.name): T? {
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

inline fun <reified T> SharedPreferencesDataSource.setAsJson(key: String, value: T) {
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

inline fun <reified T> SharedPreferencesDataSource.clearJson(key: String = T::class.java.name) {
    this.set(key, "")
}

inline fun <reified T> SharedPreferencesDataSource.getObject(key: String = T::class.java.name): Result0<T>? {
    return this.getFromJson<T>(key)?.let {
        Result0.Success(it)
    } ?: let { null }
}

inline fun <reified T> SharedPreferencesDataSource.setObject(value: T, key: String = T::class.java.name) {
    this.setAsJson(key, value)
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