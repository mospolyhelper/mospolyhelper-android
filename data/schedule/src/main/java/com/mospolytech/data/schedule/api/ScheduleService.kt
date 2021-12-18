package com.mospolytech.data.schedule.api

import com.mospolytech.data.base.retrofit.network.NetworkResponse
import com.mospolytech.domain.schedule.model.ScheduleDay
import com.mospolytech.domain.schedule.model.ScheduleSourceFull
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleService {
    @GET("/schedule/sources/{type}")
    suspend fun getSources(@Path("type") type: String): NetworkResponse<List<ScheduleSourceFull>>

    @GET("/schedules/{type}/{key}")
    suspend fun getSchedule(
        @Path("type") type: String,
        @Path("key") key: String
    ): NetworkResponse<List<ScheduleDay>>
}