package com.mospolytech.domain.schedule.usecase

import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository

class ScheduleUseCase(
    private val repository: ScheduleRepository
) {
    fun getSchedule() =
        repository.getSchedule(ScheduleSource(ScheduleSources.Group, "181-721"))

    fun getLessonsReview() =
        repository.getLessonsReview(ScheduleSource(ScheduleSources.Group, "181-721"))

    fun findFreePlaces(filters: PlaceFilters) =
        repository.findFreePlaces(filters)
}