package com.mospolytech.mospolyhelper.repository.models.schedule

import com.mospolytech.mospolyhelper.utils.CalendarUtils
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
        val empty = Group(
            "",
            0,
            CalendarUtils.getMinValue(),
            CalendarUtils.getMaxValue(),
            false,
            ""
        )
    }
}