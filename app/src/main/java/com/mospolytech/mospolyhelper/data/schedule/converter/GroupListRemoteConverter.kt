package com.mospolytech.mospolyhelper.data.schedule.converter

import com.beust.klaxon.*
import java.lang.StringBuilder

class GroupListRemoteConverter {
    companion object {
        const val GROUPS_KEY = "groups"
    }
    fun parseGroupList(groupListString: String) =
        (Parser.default().parse(StringBuilder(groupListString)) as JsonObject).array<String>(
            GROUPS_KEY
        )?.toList() ?: emptyList()
}