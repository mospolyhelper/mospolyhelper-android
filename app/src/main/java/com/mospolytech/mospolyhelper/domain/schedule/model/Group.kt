package com.mospolytech.mospolyhelper.domain.schedule.model

import java.time.LocalDate

data class Group(
    val title: String,
    val course: Int,
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val isEvening: Boolean,
    val comment: String
) {
    companion object {
        val empty =
            Group(
                "",
                0,
                LocalDate.MIN,
                LocalDate.MAX,
                false,
                ""
            )
    }
}