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
import kotlinx.coroutines.flow.flow
import retrofit2.http.Body

class ScheduleRepositoryImpl(
    private val service: ScheduleService
) : ScheduleRepository {
    override fun getSources(type: ScheduleSources) = flow {
        emit(service.getSources(type.name.lowercase()).toResult())
    }

    override fun getSchedule(source: ScheduleSource) = flow {
        emit(service.getSchedule(source.type.name.lowercase(), source.key).toResult())
    }

    override fun getLessonsReview(source: ScheduleSource) = flow {
        emit(service.getLessonsReview(source.type.name.lowercase(), source.key).toResult())
    }

    override fun findFreePlaces(filters: PlaceFilters) = flow {
        emit(service.findFreePlaces(filters).toResult())
    }
}