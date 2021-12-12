package com.mospolytech.data.base.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class ApiVersionInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain
            .request()
            .newBuilder()
            .addHeader("v",  "1")
            .build()
            .let { chain.proceed(it) }
    }
}