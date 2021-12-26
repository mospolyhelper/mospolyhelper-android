package com.mospolytech.data.schedule.api

import com.mospolytech.data.base.retrofit.network.NetworkResponse
import com.mospolytech.domain.schedule.model.lesson.LessonDateTimes
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.model.review.LessonTimesReview
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ScheduleService {
    @GET("/schedule/sources/{type}")
    suspend fun getSources(
        @Path("type") type: String
    ): NetworkResponse<List<ScheduleSourceFull>>

    @GET("/schedule/sources")
    suspend fun getSourceTypes(
    ): NetworkResponse<List<ScheduleSources>>

    @GET("/schedules/{type}/{key}")
    suspend fun getSchedule(
        @Path("type") type: String,
        @Path("key") key: String
    ): NetworkResponse<List<ScheduleDay>>

    @GET("/lessons/review/{type}/{key}")
    suspend fun getLessonsReview(
        @Path("type") type: String,
        @Path("key") key: String
    ): NetworkResponse<List<LessonTimesReview>>

    @POST("/schedule/free-place")
    suspend fun findFreePlaces(
        @Body filters: PlaceFilters
    ): NetworkResponse<Map<Place, List<LessonDateTimes>>>

}