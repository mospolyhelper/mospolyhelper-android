package com.mospolytech.data.schedule.api

import com.mospolytech.domain.schedule.model.lesson.LessonDateTimes
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.review.LessonTimesReview
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ScheduleService {
    @GET("/schedule/sources/{type}")
    fun getSources(
        @Path("type") type: String
    ): Flow<Result<List<ScheduleSourceFull>>>

    @GET("/schedule/sources")
    fun getSourceTypes(): Flow<Result<List<ScheduleSources>>>

    @GET("/schedules/{type}/{key}")
    fun getSchedule(
        @Path("type") type: String,
        @Path("key") key: String
    ): Flow<Result<List<ScheduleDay>>>

    @GET("/lessons/review/{type}/{key}")
    fun getLessonsReview(
        @Path("type") type: String,
        @Path("key") key: String
    ): Flow<Result<List<LessonTimesReview>>>

    @POST("/schedule/free-place")
    fun findFreePlaces(
        @Body filters: PlaceFilters
    ): Flow<Result<Map<Place, List<LessonDateTimes>>>>

}