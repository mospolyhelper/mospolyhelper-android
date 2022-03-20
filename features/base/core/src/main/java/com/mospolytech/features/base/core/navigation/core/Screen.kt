package com.mospolytech.features.base.core.navigation.core

import com.mospolytech.features.base.core.NativeText
import com.mospolytech.features.base.core.navigation.core.Screen.Companion.toArgString
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.capturedKClass
import kotlinx.serialization.json.Json

abstract class Screen(
    val args: Map<String, String> = emptyMap()
) {
    constructor(
        vararg args: Pair<String, Any?>
    ) : this(args.filter { it.second != null }.associate { it.first to it.second.toArgString() })

    companion object {
        private fun <T> T.toArgString(): String {
            return when(this) {
                is String -> this
                is Int -> this.toString()
                is Long -> this.toString()
                is Float -> this.toString()
                is Double -> this.toString()
                is Screen -> ScreenInfoSerializer.serialize(this)
                is NativeText -> Json.encodeToString(this as NativeText)
                else -> this.toString()
            }
        }

        inline fun <reified T> T.serialized(): String {
            return Json.encodeToString(this)
        }
    }

    inline fun <reified T> getArg(key: String): T {
        return when(T::class) {
            String::class -> args[key] as T
            Int::class -> args[key]?.toInt() as T
            Long::class -> args[key]?.toLong() as T
            Float::class -> args[key]?.toFloat() as T
            Double::class -> args[key]?.toDouble() as T
            Screen::class -> args[key]?.let { ScreenInfoSerializer.deserialize(it) } as T
            else -> args[key]?.let { Json.decodeFromString<T>(it) } as T
        }
    }

    inline fun <reified T> getArg(key: String, defaultValue: T): T {
        return getArg(key) ?: defaultValue
    }

    open val key = this::class.qualifiedName ?: ""
}