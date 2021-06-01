package com.mospolytech.mospolyhelper.di.core

import androidx.room.Room
import com.mospolytech.mospolyhelper.data.core.local.AppDatabase
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.core.migrations.MIGRATION_1_2
import com.mospolytech.mospolyhelper.data.core.repository.SharedPreferencesRepository
import com.mospolytech.mospolyhelper.domain.core.repository.PreferencesRepository
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.charset.Charset

val coreModule = module {
    single<Mediator<String, ViewModelMessage>> { Mediator<String, ViewModelMessage>() }

    single<AppDatabase> {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "database"
        ).addMigrations(MIGRATION_1_2).build()
    }

    single { SharedPreferencesDataSource(get()) }

    single<PreferencesRepository> { SharedPreferencesRepository(get()) }

    single(named("accountClient")) {
        HttpClient {
            Charsets {
                register(Charset.forName("Windows-1251"))
            }
        }
    }

    single(named("accountHerokuClient")) {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    single(named("schedule")) {
        HttpClient()
    }
}