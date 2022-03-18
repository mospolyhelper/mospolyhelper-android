package com.mospolytech.data.base.utils.retrofit.call_adapters.common

import com.mospolytech.data.base.utils.retrofit.network.errors.ApiError
import com.mospolytech.data.base.utils.retrofit.network.errors.NetworkError
import com.mospolytech.data.base.utils.retrofit.network.errors.UnknownResponseError
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

class ResponseMapper<T : Any>(
    private val errorConverter: Converter<ResponseBody, T>,
    private val onResponse: (code: Int, body: Any?) -> Unit
) {
    fun mapResponse(response: Response<T>): Result<T> {
        return processSuccess(response)
    }

    fun mapFailure(throwable: Throwable): Result<T> {
        return processFailure(throwable)
    }

    private fun processFailure(throwable: Throwable): Result<T> {
        val error = when (throwable) {
            is IOException -> NetworkError(throwable)
            else -> UnknownResponseError(throwable)
        }
        return Result.failure(error)
    }

    private fun processSuccess(response: Response<T>): Result<T> {
        val body = response.body()
        val code = response.code()
        val error = response.errorBody()

        if (response.isSuccessful) {
            onResponse(code, body)
            return body?.let { Result.success(it) } ?: Result.failure(UnknownResponseError(null))
        } else {
            val errorBody = when {
                error == null -> null
                error.contentLength() == 0L -> null
                else -> try {
                    errorConverter.convert(error)
                } catch (ex: Exception) {
                    null
                }
            }

            onResponse(code, errorBody)

            return if (errorBody != null) {
                Result.failure(ApiError(errorBody, code))
            } else {
                Result.failure(UnknownResponseError(null))
            }
        }
    }
}