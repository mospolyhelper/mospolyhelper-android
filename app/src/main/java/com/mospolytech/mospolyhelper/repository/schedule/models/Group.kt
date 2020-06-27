package com.mospolytech.mospolyhelper.repository.schedule.models

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