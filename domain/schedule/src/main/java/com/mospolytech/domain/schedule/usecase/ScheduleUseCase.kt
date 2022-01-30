package com.mospolytech.domain.schedule.usecase

import com.mospolytech.domain.base.utils.Lce
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.review.LessonTimesReview
import com.mospolytech.domain.schedule.model.schedule.LessonsByTime
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest
import java.time.LocalDate

class ScheduleUseCase(
    private val repository: ScheduleRepository
) {
    fun getSourceTypes() =
        repository.getSourceTypes()

    fun getSources(type: ScheduleSources) =
        repository.getSources(type)

    suspend fun setSelectedSource(source: ScheduleSourceFull) =
        repository.setSelectedSource(source)

    fun getSelectedSource() =
        repository.getSelectedSource()

    fun getSchedule() =
        repository.getSelectedSource()
            .transformLatest {
                val source = it.getOrNull()
                if (source == null) {
                    emit(Lce.failure<List<ScheduleDay>>(Exception()))
                } else {
                    emitAll(repository.getSchedule(ScheduleSource(source.type, source.key)))
                }
            }


    fun getLessonsReview() =
        repository.getSelectedSource()
            .transformLatest {
                val source = it.getOrNull()
                if (source == null) {
                    emit(Result.failure<List<LessonTimesReview>>(Exception()))
                } else {
                    emitAll(repository.getLessonsReview(ScheduleSource(source.type, source.key)))
                }
            }

    fun findFreePlaces(filters: PlaceFilters) =
        repository.findFreePlaces(filters)

    fun getScheduleDay(schedule: List<ScheduleDay>, date: LocalDate): List<LessonsByTime> {
        return schedule.firstOrNull { it.date == date }?.lessons ?: emptyList()
    }
}