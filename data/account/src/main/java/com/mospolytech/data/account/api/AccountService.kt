package com.mospolytech.data.account.api

import com.mospolytech.domain.account.model.*
import com.mospolytech.domain.base.model.PagingDTO
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountService {

    @GET("/peoples/classmates")
    fun getClassmates(): Flow<Result<List<Student>>>

    @GET("/peoples/students/{name}")
    fun getStudents(
        @Path("name") name: String?, @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Flow<Result<PagingDTO<Student>>>

    @GET("/peoples/teachers/{name}")
    fun getTeachers(
        @Path("name") name: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Flow<Result<PagingDTO<Teacher>>>

    @GET("/applications")
    fun getApplications(): Flow<Result<List<Application>>>

    @GET("/performance")
    fun getMarks(): Flow<Result<List<Marks>>>

    @GET("/performance/semesters")
    fun getSemesters(): Flow<Result<List<Int>>>

    @GET("/performance/semesters/{semester}")
    fun getMarksBySemester(
        @Path("semester") semester: String
    ): Flow<Result<Marks>>

    @GET("/performance/courses")
    fun getCourses(): Flow<Result<List<Int>>>

    @GET("/performance/courses/{course}")
    fun getMarksByCourse(
        @Path("course") course: String
    ): Flow<Result<List<Marks>>>

    @GET("/personal")
    fun getPersonalInfo(): Flow<Result<Personal>>

    @GET("/personal/orders")
    fun getOrders(): Flow<Result<List<Order>>>

    @GET("/payments")
    fun getPayments(): Flow<Result<List<Payments>>>

    @GET("/payments/types")
    fun getPaymentsTypes(): Flow<Result<List<PaymentType>>>

    @GET("/payment/{type}")
    fun getPayment(
        @Path("type") type: String
    ): Flow<Result<Payments>>

}