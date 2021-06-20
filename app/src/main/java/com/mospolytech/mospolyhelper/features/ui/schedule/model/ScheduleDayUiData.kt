package com.mospolytech.mospolyhelper.features.ui.schedule.model

import java.time.LocalDate

data class ScheduleDayUiData(
    val date: LocalDate,
    val orderMap: Map<Int, Boolean>
)