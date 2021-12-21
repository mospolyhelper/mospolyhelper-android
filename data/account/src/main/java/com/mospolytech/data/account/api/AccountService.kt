package com.mospolytech.data.account.api

import com.mospolytech.data.account.model.Response
import com.mospolytech.data.base.retrofit.network.NetworkResponse
import com.mospolytech.domain.account.model.Student
import retrofit2.http.GET
import retrofit2.http.Query

interface AccountService {

    @GET("/peoples/classmates/{name}")
    fun getStudents(@Query("name") name: String?): NetworkResponse<Response<Student>>
}