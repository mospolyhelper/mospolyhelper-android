package com.mospolytech.domain.schedule.repository

import com.mospolytech.domain.schedule.model.*
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSources(type: ScheduleSources): Flow<Result<List<ScheduleSourceFull>>>
    fun getSchedule(source: ScheduleSource): Flow<Result<List<ScheduleDay>>>
    fun getLessonsReview(source: ScheduleSource): Flow<Result<List<LessonTimesReview>>>
}