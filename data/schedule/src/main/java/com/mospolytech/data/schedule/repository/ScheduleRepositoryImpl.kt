package com.mospolytech.data.schedule.repository

import com.mospolytech.data.base.Versions
import com.mospolytech.data.base.local.DataVersionLocalDS
import com.mospolytech.data.base.local.isExpired
import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.data.schedule.api.ScheduleService
import com.mospolytech.data.schedule.local.ScheduleLocalDS
import com.mospolytech.domain.base.utils.loading
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

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

    override suspend fun setSelectedSource(source: ScheduleSourceFull) = withContext(Dispatchers.IO) {
        local.setSelectedSource(source)
    }

    override fun getSelectedSource() = flow {
        val q = local.getSelectedSource()
        emit(q)
    }.flowOn(Dispatchers.IO)

    override fun getSchedule(source: ScheduleSource, forceUpdate: Boolean) = flow {
        val cachedSchedule = local.getSchedule(source)
        val needUpdate = versionDS.isExpired(1.days, Versions.Schedule, source.id) || forceUpdate
        emit(cachedSchedule.loading(needUpdate))

        if (needUpdate) {
            val newSchedule = service.getSchedule(source.type.name.lowercase(), source.key).toResult()
            local.saveSchedule(source, newSchedule.getOrNull())
            versionDS.setVersion(ZonedDateTime.now(), Versions.Schedule, source.id)
            emit(newSchedule.loading(false))
        }
    }.flowOn(Dispatchers.IO)

    override fun getLessonsReview(source: ScheduleSource) = flow {
        emit(service.getLessonsReview(source.type.name.lowercase(), source.key).toResult())
    }.flowOn(Dispatchers.IO)

    override fun findFreePlaces(filters: PlaceFilters) = flow {
        emit(service.findFreePlaces(filters).toResult())
    }.flowOn(Dispatchers.IO)

}