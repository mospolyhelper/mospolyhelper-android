package com.mospolytech.mospolyhelper.di.utilities.news

import com.mospolytech.mospolyhelper.data.utilities.news.api.UniversityNewsApi
import com.mospolytech.mospolyhelper.data.utilities.news.converter.NewsConverter
import com.mospolytech.mospolyhelper.data.utilities.news.remote.NewsPagingSource
import com.mospolytech.mospolyhelper.data.utilities.news.repository.NewsRepositoryImpl
import com.mospolytech.mospolyhelper.di.utils.DiConstants
import com.mospolytech.mospolyhelper.domain.utilities.news.repository.NewsRepository
import com.mospolytech.mospolyhelper.domain.utilities.news.usecase.NewsUseCase
import com.mospolytech.mospolyhelper.features.ui.utilities.news.NewsViewModel
import com.mospolytech.mospolyhelper.utils.CustomTrust
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val newsModule = module {
    single(named(DiConstants.NEWS_CLIENT)) {
        HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            defaultRequest {
                header("X-Requested-With", "XMLHttpRequest")
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            val customTrust = try {
                CustomTrust()
            } catch (e: Exception) {
                null
            }
            customTrust?.let {
                engine {
                    config {
                        sslSocketFactory(it.sslSocketFactory, it.trustManager)
                    }
                }
            }
        }
    }

    single { UniversityNewsApi(get(named(DiConstants.NEWS_CLIENT))) }
    single { NewsConverter() }
    single { NewsPagingSource(get(), get()) }
    single<NewsRepository> { NewsRepositoryImpl(get()) }
    single { NewsUseCase(get()) }
    viewModel { NewsViewModel(get()) }
}