package com.mospolytech.data.base.retrofit

import com.mospolytech.data.base.PreferencesDataSource
import com.mospolytech.domain.base.PrefKeys
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val preferences: PreferencesDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferences.get(PrefKeys.Token, "")
        return if (token.isEmpty()) {
            chain
                .request()
                .let { chain.proceed(it) }
        } else {
            chain
                .request()
                .newBuilder()
                .addHeader(PrefKeys.Token,  token)
                .build()
                .let { chain.proceed(it) }
        }
    }
}