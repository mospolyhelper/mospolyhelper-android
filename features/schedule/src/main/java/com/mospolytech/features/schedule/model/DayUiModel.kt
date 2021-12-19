package com.mospolytech.features.schedule.model

import java.time.LocalDate

data class DayUiModel(
    val date: LocalDate,
    val lessonCount: Int
)