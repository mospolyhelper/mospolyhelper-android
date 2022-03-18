package com.mospolytech.data.base.utils.retrofit

import com.mospolytech.data.base.utils.retrofit.call_adapters.flow_result.FlowResultCallAdapterFactory

import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

inline fun <reified T> buildRetrofitService(retrofit: Retrofit): T {
    return retrofit.create(T::class.java)
}

fun buildRetrofitBuilder(client: okhttp3.OkHttpClient, baseUrl: String = ""): Retrofit.Builder {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(
            JsonConverterFactory(
                Json {
                    ignoreUnknownKeys = true
                    allowStructuredMapKeys = true
                }
            )
        )
        .addCallAdapterFactory(FlowResultCallAdapterFactory { code: Int, body: Any? -> })
        .client(client)
}

fun <T> Call<T>.registerCallback(
    onSuccess: (response: Response<T>) -> Unit,
    onFailure: (t: Throwable) -> Unit
) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            onSuccess(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(t)
        }
    })
}