package com.mospolytech.mospolyhelper.repository.model.schedule

import java.util.*

data class Group(
    val title: String,
    val course: Int,
    val dateFrom: Calendar,
    val dateTo: Calendar,
    val isEvening: Boolean,
    val comment: String
) {
    companion object {
        val empty by lazy {
            Group(
                "",
                0,
                Calendar.getInstance().apply { time = Date(Long.MIN_VALUE) },
                Calendar.getInstance().apply { time = Date(Long.MAX_VALUE) },
                false,
                ""
            )
        }
    }
}