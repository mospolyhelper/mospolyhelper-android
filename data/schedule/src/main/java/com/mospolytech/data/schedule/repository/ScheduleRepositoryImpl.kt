package com.mospolytech.data.schedule.repository

import com.mospolytech.data.base.VersionConsts
import com.mospolytech.data.base.local.DataVersionLocalDS
import com.mospolytech.data.base.local.isExpired
import com.mospolytech.data.base.model.isExpired
import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.data.schedule.api.ScheduleService
import com.mospolytech.data.schedule.local.ScheduleLocalDS
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.ZonedDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ScheduleRepositoryImpl(
    private val service: ScheduleService,
    private val local: ScheduleLocalDS,
    private val versionDS: DataVersionLocalDS
) : ScheduleRepository {
    override fun getSourceTypes() = flow {
        emit(service.getSourceTypes().toResult())
    }.flowOn(Dispatchers.IO)

    override fun getSources(type: ScheduleSources) = flow {
        emit(service.getSources(type.name.lowercase()).toResult())
    }.flowOn(Dispatchers.IO)

    override fun getSchedule(source: ScheduleSource, forceUpdate: Boolean) = flow {
        val cachedSchedule = local.getSchedule(source).getOrNull()
        // TODO: Fix if failure download and null local but is not expired
        cachedSchedule?.let {
            emit(Result.success(it))
        }

        val isExpired = versionDS.isExpired(
            1.toDuration(DurationUnit.DAYS),
            VersionConsts.Schedule,
            source.id
        )

        if (isExpired || forceUpdate) {
            val schedule = service.getSchedule(source.type.name.lowercase(), source.key).toResult()
            local.saveSchedule(source, schedule.getOrNull())
            versionDS.setVersion(ZonedDateTime.now(), VersionConsts.Schedule, source.id)
            emit(schedule)
        }
    }.flowOn(Dispatchers.IO)

    override fun getLessonsReview(source: ScheduleSource) = flow {
        emit(service.getLessonsReview(source.type.name.lowercase(), source.key).toResult())
    }.flowOn(Dispatchers.IO)

    override fun findFreePlaces(filters: PlaceFilters) = flow {
        emit(service.findFreePlaces(filters).toResult())
    }.flowOn(Dispatchers.IO)

}