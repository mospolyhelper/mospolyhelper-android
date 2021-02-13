package com.mospolytech.mospolyhelper.data.schedule.model

import com.mospolytech.mospolyhelper.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable()
class ApiGroup(
    val title: String,
    val course: Int,
    @Serializable(with = LocalDateSerializer::class)
    val dateFrom: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val dateTo: LocalDate,
    val evening: Int,
    val comment: String
)