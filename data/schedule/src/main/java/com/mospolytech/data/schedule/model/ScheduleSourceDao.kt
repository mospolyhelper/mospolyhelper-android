package com.mospolytech.data.schedule.model

import com.mospolytech.domain.schedule.model.source.ScheduleSource
import kotlinx.serialization.Serializable
import org.kodein.db.model.orm.Metadata

@Serializable
data class ScheduleSourceDao(
    override val id: String,
    val source: ScheduleSource
) : Metadata {
    companion object {
        fun from(source: ScheduleSource) =
            ScheduleSourceDao(
                source.id,
                source
            )
    }

    fun toModel() = source
}