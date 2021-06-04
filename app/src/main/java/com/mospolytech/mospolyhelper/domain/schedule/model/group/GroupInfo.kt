package com.mospolytech.mospolyhelper.domain.schedule.model.group

import java.time.LocalDate

class GroupInfo(
    val group: Group,
    val course: Int,
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val comment: String
) {
    companion object {
        val empty = GroupInfo(
            Group.empty,
            0,
            LocalDate.MIN,
            LocalDate.MAX,
            ""
        )
    }
}