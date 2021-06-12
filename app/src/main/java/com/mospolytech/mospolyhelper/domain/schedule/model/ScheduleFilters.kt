package com.mospolytech.mospolyhelper.domain.schedule.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
class ScheduleFilters(
    val titles: Set<String>,
    val types: Set<String>,
    val teachers: Set<String>,
    val groups: Set<String>,
    val auditoriums: Set<String>,
) : Parcelable