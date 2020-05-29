package com.mospolytech.mospolyhelper.repository.remote.schedule

import com.beust.klaxon.*
import java.lang.StringBuilder

class GroupListJsonParser {
    fun parseGroupList(groupListString: String) =
        (Parser.default().parse(StringBuilder(groupListString)) as JsonArray<*>).map { it.toString() }
}