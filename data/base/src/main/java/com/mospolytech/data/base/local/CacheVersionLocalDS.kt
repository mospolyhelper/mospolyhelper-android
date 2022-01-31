package com.mospolytech.data.base.local

import com.mospolytech.data.base.model.Cached
import java.time.ZonedDateTime
import kotlin.time.Duration

class CacheVersionLocalDS(
    val cache: CacheLocalDS,
    val version: DataVersionLocalDS
) {
    val prefix = "__cache__"

    inline fun <reified T> save(obj: T, key: String): Result<Unit> {
        return cache.save(obj, key).onSuccess {
            version.setVersion(ZonedDateTime.now(), prefix + key)
        }
    }

    inline fun <reified T> get(key: String, expire: Duration): Cached<T?> {
        val obj = cache.get<T>(key)
        val isExpired = version.isExpired(expire, prefix + key)
        return Cached(obj, isExpired)
    }
}