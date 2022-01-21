package com.mospolytech.data.schedule.local

import com.mospolytech.data.schedule.model.ScheduleDao
import com.mospolytech.data.schedule.model.ScheduleSourceDao
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import org.kodein.db.DB
import org.kodein.db.getById

class ScheduleLocalDS(
    private val db: DB
) {
    fun saveSchedule(source: ScheduleSource, schedule: List<ScheduleDay>?) {
        db.put(
            ScheduleDao.from(source, schedule)
        )
    }

    fun getSchedule(source: ScheduleSource): Result<List<ScheduleDay>?> {
        return runCatching {
            //val key = db.keyFrom(source.id)
            db.getById<ScheduleDao>(source.id)?.days
        }
    }
}