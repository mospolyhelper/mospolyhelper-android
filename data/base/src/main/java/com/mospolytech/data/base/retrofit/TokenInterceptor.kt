package com.mospolytech.data.base.retrofit

import com.mospolytech.data.base.local.PreferencesDS
import com.mospolytech.data.base.local.get
import com.mospolytech.domain.base.PrefKeys
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val preferences: PreferencesDS
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