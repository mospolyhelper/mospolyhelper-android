package com.mospolytech.data.base

import com.mospolytech.data.base.retrofit.ApiVersionInterceptor
import com.mospolytech.data.base.retrofit.JsonConverterFactory
import com.mospolytech.data.base.retrofit.TokenInterceptor
import com.mospolytech.data.base.retrofit.network.NetworkResponseAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

val baseDataModule = module {
    single { TokenInterceptor(get()) }
    single { ApiVersionInterceptor() }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    single { OkHttpClient.Builder() }
    single {
        get<OkHttpClient.Builder>()
            .addInterceptor(get<TokenInterceptor>())
            .addInterceptor(get<ApiVersionInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://mph-schedule.herokuapp.com/")
            .addConverterFactory(JsonConverterFactory())
            .addCallAdapterFactory(NetworkResponseAdapterFactory { code: Int, body: Any? ->
                GlobalScope.launch(Dispatchers.IO) {
//                    get<EventRepository>().codeResponse
//                        .emit(code to (body as? Response<*>)?.error?.errorCode)
                }
            })
            .client(get())
    }

    single<Retrofit> { get<Retrofit.Builder>().build() }

    single { EventRepository() }
}