package com.mospolytech.data.schedule.model

import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import kotlinx.serialization.Serializable
import org.kodein.db.model.orm.Metadata

@Serializable
data class ScheduleDao(
    override val id: String,
    val days: List<ScheduleDay>?
): Metadata {
    companion object {
        fun from(scheduleSource: ScheduleSource, schedule: List<ScheduleDay>?) =
            ScheduleDao(scheduleSource.id, schedule)
    }

    fun toModel() = days
}