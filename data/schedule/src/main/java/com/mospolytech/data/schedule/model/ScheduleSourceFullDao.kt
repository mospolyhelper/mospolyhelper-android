package com.mospolytech.data.schedule.model

import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import kotlinx.serialization.Serializable
import org.kodein.db.model.orm.Metadata

@Serializable
data class ScheduleSourceFullDao(
    override val id: String,
    val source: ScheduleSourceFull
) : Metadata {
    companion object {
        fun from(source: ScheduleSourceFull, id: String) =
            ScheduleSourceFullDao(
                id,
                source
            )
    }

    fun toModel() = source
}