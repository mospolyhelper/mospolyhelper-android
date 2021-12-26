package com.mospolytech.data.schedule.repository

import com.mospolytech.data.base.retrofit.network.NetworkResponse
import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.data.schedule.api.ScheduleService
import com.mospolytech.domain.schedule.model.lesson.LessonDateTimes
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.http.Body

class ScheduleRepositoryImpl(
    private val service: ScheduleService
) : ScheduleRepository {
    override fun getSourceTypes() = flow {
        emit(service.getSourceTypes().toResult())
    }.flowOn(Dispatchers.IO)

    override fun getSources(type: ScheduleSources) = flow {
        emit(service.getSources(type.name.lowercase()).toResult())
    }.flowOn(Dispatchers.IO)

    override fun getSchedule(source: ScheduleSource) = flow {
        emit(service.getSchedule(source.type.name.lowercase(), source.key).toResult())
    }.flowOn(Dispatchers.IO)

    override fun getLessonsReview(source: ScheduleSource) = flow {
        emit(service.getLessonsReview(source.type.name.lowercase(), source.key).toResult())
    }.flowOn(Dispatchers.IO)

    override fun findFreePlaces(filters: PlaceFilters) = flow {
        emit(service.findFreePlaces(filters).toResult())
    }.flowOn(Dispatchers.IO)

}