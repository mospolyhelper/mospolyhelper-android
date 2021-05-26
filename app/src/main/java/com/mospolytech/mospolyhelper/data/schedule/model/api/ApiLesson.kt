package com.mospolytech.mospolyhelper.data.schedule.model.api

import com.mospolytech.mospolyhelper.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class ApiLesson(
    val sbj: String,
    val teacher: String,
    @Serializable(with = LocalDateSerializer::class)
    val dts: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val df: LocalDate,
    val auditories: List<ApiAuditorium>,
    val type: String,
)