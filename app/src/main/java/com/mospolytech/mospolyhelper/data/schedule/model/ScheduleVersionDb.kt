package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import java.time.ZonedDateTime

@Entity
data class ScheduleVersionDb(
    @PrimaryKey
    val userScheduleId: String,
    val downloadingDateTime: ZonedDateTime
)

