package com.mospolytech.data.base.retrofit.network

import java.io.IOException

sealed interface NetworkResponse<out T : Any> {
    /**
     * Success response with body
     */
    data class Success<T : Any>(val body: T) : NetworkResponse<T>

    sealed class Error : Exception(), NetworkResponse<Nothing> {
        /**
         * Failure response with body
         */
        data class ApiError(val body: Any, val code: Int) : Error()
        // (val body: Response<Any>, val code: Int)

        /**
         * Network error
         */
        data class NetworkError(val error: IOException) : Error()

        /**
         * For example, json parsing error
         */
        data class UnknownError(val error: Throwable?) : Error()
    }
}