package com.mospolytech.mospolyhelper.data.schedule.converter

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GroupListRemoteConverter {
    companion object {
        const val GROUPS_KEY = "groups"
    }
    fun parseGroupList(groupListString: String) =
        Json.parseToJsonElement(groupListString)
            .jsonObject[GROUPS_KEY]
            ?.jsonObject?.map { it.key }
            ?: emptyList()
}