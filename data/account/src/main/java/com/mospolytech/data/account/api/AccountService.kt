package com.mospolytech.data.account.api

import com.mospolytech.data.base.retrofit.network.NetworkResponse
import com.mospolytech.domain.account.model.*
import com.mospolytech.domain.base.model.PagingDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountService {

    @GET("/peoples/classmates/{name}")
    suspend fun getClassmates(@Path("name") name: String?): NetworkResponse<List<Student>>

    @GET("/peoples/students/{name}")
    suspend fun getStudents(@Path("name") name: String?, @Query("page") page: Int,
                    @Query("pageSize") pageSize: Int): NetworkResponse<PagingDTO<Student>>

    @GET("/peoples/teachers/{name}")
    suspend fun getTeachers(@Path("name") name: String?, @Query("page") page: Int,
                    @Query("pageSize") pageSize: Int): NetworkResponse<PagingDTO<Teacher>>

    @GET("/applications")
    suspend fun getApplications(): NetworkResponse<List<Application>>

    @GET("/performance")
    suspend fun getMarks(): NetworkResponse<List<Marks>>

    @GET("/performance/semesters")
    suspend fun getSemesters(): NetworkResponse<List<Int>>

    @GET("/performance/semesters/{semester}")
    suspend fun getMarksBySemester(@Path("semester") semester: String): NetworkResponse<Marks>

    @GET("/performance/courses")
    suspend fun getCourses(): NetworkResponse<List<Int>>

    @GET("/performance/courses/{course}")
    suspend fun getMarksByCourse(@Path("course") course: String): NetworkResponse<List<Marks>>

    @GET("/personal")
    suspend fun getPersonalInfo(): NetworkResponse<Personal>

    @GET("/personal/orders")
    suspend fun getOrders(): NetworkResponse<List<Order>>

    @GET("/payments")
    suspend fun getPayments(): NetworkResponse<List<Payments>>

    @GET("/payments/types")
    suspend fun getPaymentsTypes(): NetworkResponse<List<PaymentType>>

    @GET("/payment/{type}")
    suspend fun getPayment(@Path("type") type: String): NetworkResponse<Payments>

}