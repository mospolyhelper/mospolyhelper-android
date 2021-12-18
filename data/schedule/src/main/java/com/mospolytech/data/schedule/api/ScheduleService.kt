package com.mospolytech.data.schedule.api

import com.mospolytech.data.base.retrofit.network.NetworkResponse
import com.mospolytech.domain.schedule.model.ScheduleSourceFull
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleService {
    @GET("/schedule/sources/{type}")
    suspend fun getSources(@Path("type") type: String): NetworkResponse<List<ScheduleSourceFull>>
}