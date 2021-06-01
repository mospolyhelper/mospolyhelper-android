package com.mospolytech.mospolyhelper.data.utils

import com.mospolytech.mospolyhelper.data.core.local.AssetsDataSource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

inline fun <reified T> AssetsDataSource.getFromJson(key: String): T? {
    return try {
        val json = get(key) ?: ""
        if (json.isEmpty()) {
            null
        } else {
            Json.decodeFromString<T>(json)
        }
    } catch (e: Exception) {
        null
    }
}