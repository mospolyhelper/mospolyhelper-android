package com.mospolytech.data.schedule.local

import com.mospolytech.data.schedule.model.ScheduleDao
import com.mospolytech.data.schedule.model.ScheduleSourceDao
import com.mospolytech.data.schedule.model.ScheduleSourceFullDao
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import org.kodein.db.*
import org.kodein.memory.util.isFrozen

class ScheduleLocalDS(
    private val db: DB
) {
    fun saveSchedule(source: ScheduleSource, schedule: List<ScheduleDay>?) {
        db.put(
            ScheduleDao.from(source, schedule)
        )
    }

    fun getSchedule(source: ScheduleSource): Result<List<ScheduleDay>> {
        return runCatching {
            //val key = db.keyFrom(source.id)
            db.getById<ScheduleDao>(source.id)?.days ?: emptyList()
        }
    }

    fun setSelectedSource(source: ScheduleSourceFull) {
        db.put(
            ScheduleSourceFullDao.from(source, "Selected")
        )
    }

    fun getSelectedSource(): Result<ScheduleSourceFull?> {
        return runCatching {
            val a = db.flowOf(db.keyById<ScheduleSourceFullDao>("Selected"))
            db.getById<ScheduleSourceFullDao>("Selected")?.source
        }
    }
}