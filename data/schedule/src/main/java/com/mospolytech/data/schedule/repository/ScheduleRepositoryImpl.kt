package com.mospolytech.data.schedule.repository

import com.mospolytech.data.base.consts.CacheConst
import com.mospolytech.data.base.consts.PrefConst
import com.mospolytech.data.base.local.*
import com.mospolytech.data.schedule.api.ScheduleService
import com.mospolytech.data.schedule.model.ScheduleDao
import com.mospolytech.domain.base.utils.loading
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.days

class ScheduleRepositoryImpl(
    private val service: ScheduleService,
    private val cachedDS: CacheVersionLocalDS,
    private val preferencesDS: PreferencesDS
) : ScheduleRepository {
    override fun getSourceTypes() =
        service.getSourceTypes()
            .flowOn(Dispatchers.IO)

    override fun getSources(type: ScheduleSources) =
        service.getSources(type.name.lowercase())
            .flowOn(Dispatchers.IO)

    override suspend fun setSelectedSource(source: ScheduleSourceFull): Unit =
        withContext(Dispatchers.IO) {
            preferencesDS.set(source, PrefConst.SelectedScheduleSource)
        }

    override fun getSelectedSource() = preferencesDS
        .flowOf<ScheduleSourceFull>(PrefConst.SelectedScheduleSource)
        .flowOn(Dispatchers.IO)

    override fun getSchedule(source: ScheduleSource, forceUpdate: Boolean) = flow {
        val (cachedSchedule, isExpired) = cachedDS.get<ScheduleDao>(CacheConst.Schedule, 1.days)
        emit(cachedSchedule.map { it?.days ?: emptyList() }.loading(isExpired || forceUpdate))

        if (isExpired || forceUpdate) {
            val newSchedule = service.getSchedule(source.type.name.lowercase(), source.key).first()
            newSchedule.onSuccess {
                cachedDS.save(ScheduleDao.from(source, it), CacheConst.Schedule)
            }
            emit(newSchedule.loading(false))
        }
    }.flowOn(Dispatchers.IO)

    override fun getLessonsReview(source: ScheduleSource) =
        service.getLessonsReview(source.type.name.lowercase(), source.key)
            .flowOn(Dispatchers.IO)

    override fun findFreePlaces(filters: PlaceFilters) =
        service.findFreePlaces(filters)
            .flowOn(Dispatchers.IO)
}