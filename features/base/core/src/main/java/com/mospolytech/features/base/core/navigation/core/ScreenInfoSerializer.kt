package com.mospolytech.features.base.core.navigation.core

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ScreenInfoSerializer {
    fun serialize(screen: Screen): String {
        val pair = screen.key to screen.args
        return Json.encodeToString(pair)
    }

    fun deserialize(text: String): ScreenInfo? {
        return try {
            val pair: Pair<String, Map<String, String>> = Json.decodeFromString(text)
            ScreenInfo(
                pair.first,
                pair.second
            )
        } catch (e: Exception) {
            null
        }
    }
}