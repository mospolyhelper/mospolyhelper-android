package com.mospolytech.mospolyhelper.data.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

fun JsonElement.array(key: String): JsonArray? {
    return jsonObject[key]?.jsonArray
}

fun JsonElement.boolean(key: String): Boolean? {
    return jsonObject[key]?.jsonPrimitive?.boolean
}

fun JsonElement.int(key: String): Int? {
    return jsonObject[key]?.jsonPrimitive?.int
}

fun JsonElement.string(key: String): String? {
    return jsonObject[key]?.jsonPrimitive?.content
}

fun JsonElement.stringOrNull(key: String): String? {
    return jsonObject[key]?.jsonPrimitive?.contentOrNull
}

fun JsonElement.string(): String {
    return jsonPrimitive.content
}

inline fun<reified T> String.toObject(): T = Json.decodeFromString(this)