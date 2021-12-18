package com.mospolytech.data.base.retrofit

import com.mospolytech.data.base.retrofit.network.NetworkResponse

//fun <T: Any> NetworkResponse<Response<T>>.toResult(): Result<T> {
//    return when (this) {
//        is NetworkResponse.Success -> if (this.body.isSuccess && this.body.response != null) {
//            Result.success(this.body.response)
//        } else if (!this.body.isSuccess && this.body.error != null) {
//            Result.failure(NetworkResponse.Error.ApiError(this.body as Response<Any>, 400))
//        } else {
//            Result.failure(Exception("Unknown envelope exception: " + this.body.toString()))
//        }
//        is NetworkResponse.Error -> Result.failure(this)
//    }
//}

fun <T: Any> NetworkResponse<T>.toResult(): Result<T> {
    return when (this) {
        is NetworkResponse.Success -> Result.success(this.body)
        is NetworkResponse.Error -> Result.failure(this)
    }
}