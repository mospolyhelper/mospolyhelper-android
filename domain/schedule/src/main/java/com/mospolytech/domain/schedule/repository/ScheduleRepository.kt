package com.mospolytech.domain.schedule.repository

import com.mospolytech.domain.schedule.model.lesson.LessonDateTimes
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.review.LessonTimesReview
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.source.ScheduleSource
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSources(type: ScheduleSources): Flow<Result<List<ScheduleSourceFull>>>
    fun getSchedule(source: ScheduleSource): Flow<Result<List<ScheduleDay>>>
    fun getLessonsReview(source: ScheduleSource): Flow<Result<List<LessonTimesReview>>>
    fun findFreePlaces(filters: PlaceFilters): Flow<Result<Map<Place, List<LessonDateTimes>>>>
}