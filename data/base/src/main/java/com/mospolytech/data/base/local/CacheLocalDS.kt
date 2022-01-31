package com.mospolytech.data.base.local

import com.mospolytech.data.base.model.CacheDao
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kodein.db.DB
import org.kodein.db.getById

class CacheLocalDS(
    private val db: DB
) {
    inline fun <reified T> save(obj: T, key: String): Result<Unit> {
        return kotlin.runCatching {
            val str = Json.encodeToString(obj)
            save(str, key)
        }
    }

    fun save(str: String, key: String) {
        db.put(CacheDao(key, str))
    }

    inline fun <reified T> get(key: String): Result<T?> {
        val json = getJson(key)
        return json.mapCatching { it?.let { Json.decodeFromString(it.value) } }
    }

    fun getJson(key: String): Result<CacheDao?> {
        return kotlin.runCatching {
            db.getById(key)
        }
    }
}