package com.mospolytech.data.base

import com.mospolytech.data.base.consts.DiConst
import com.mospolytech.data.base.local.*
import com.mospolytech.data.base.repository.EventRepository
import com.mospolytech.data.base.retrofit.ApiVersionInterceptor
import com.mospolytech.data.base.retrofit.JsonConverterFactory
import com.mospolytech.data.base.retrofit.TokenInterceptor
import com.mospolytech.data.base.retrofit.network.NetworkResponseAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

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
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(get<TokenInterceptor>())
            .addInterceptor(get<ApiVersionInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single(named(DiConst.Schedule)) {
        Retrofit.Builder()
            .baseUrl("https://mph-schedule.herokuapp.com/")
            .addConverterFactory(
                JsonConverterFactory(
                    Json {
                        ignoreUnknownKeys = true
                        allowStructuredMapKeys = true
                    }
                )
            )
            .addCallAdapterFactory(NetworkResponseAdapterFactory { code: Int, body: Any? ->
                GlobalScope.launch(Dispatchers.IO) {
//                    get<EventRepository>().codeResponse
//                        .emit(code to (body as? Response<*>)?.error?.errorCode)
                }
            })
            .client(get())
    }

    single<Retrofit>(named(DiConst.Schedule)) { get<Retrofit.Builder>(named(DiConst.Schedule)).build() }


    single(named(DiConst.Account)) {
        Retrofit.Builder()
            .baseUrl("https://mph-account.herokuapp.com/")
            .addConverterFactory(
                JsonConverterFactory(
                    Json {
                        ignoreUnknownKeys = true
                        allowStructuredMapKeys = true
                    }
                )
            )
            .addCallAdapterFactory(NetworkResponseAdapterFactory { code: Int, body: Any? ->
                GlobalScope.launch(Dispatchers.IO) {
//                    get<EventRepository>().codeResponse
//                        .emit(code to (body as? Response<*>)?.error?.errorCode)
                }
            })
            .client(get())
    }

    single<Retrofit>(named(DiConst.Account)) { get<Retrofit.Builder>(named(DiConst.Account)).build() }

    single { EventRepository() }
    single { DataVersionLocalDS(get()) }
    single<PreferencesDS> { PreferencesLocalDS(get()) }
    single { CacheLocalDS(get()) }
    single { CacheVersionLocalDS(get(), get()) }
}