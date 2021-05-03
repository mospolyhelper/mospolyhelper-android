package com.mospolytech.mospolyhelper.domain.schedule.model.info

import com.mospolytech.mospolyhelper.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class GroupsInfo(
    @Serializable(with = LocalDateSerializer::class)
    val version: LocalDate,
    val content: Map<String, GroupInfo>
)