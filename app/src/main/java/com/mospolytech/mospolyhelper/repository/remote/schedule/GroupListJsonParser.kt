package com.mospolytech.mospolyhelper.repository.remote.schedule

import com.beust.klaxon.*

class GroupListJsonParser {
    fun parseGroupList(groupListString: String) =
        (Parser.default().parse(groupListString) as JsonArray<*>).map { it.toString() }
}