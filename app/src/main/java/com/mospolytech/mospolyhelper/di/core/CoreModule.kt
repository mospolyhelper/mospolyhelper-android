package com.mospolytech.mospolyhelper.di.core

import androidx.room.Room
import com.mospolytech.mospolyhelper.data.core.local.AppDatabase
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import org.koin.dsl.module

val coreModule = module {
    single<Mediator<String, ViewModelMessage>> { Mediator<String, ViewModelMessage>() }

    single<AppDatabase> {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "database"
        ).build()
    }

    single { SharedPreferencesDataSource(get()) }
}