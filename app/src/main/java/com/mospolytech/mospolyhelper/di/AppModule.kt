package com.mospolytech.mospolyhelper.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.data.core.local.AssetsDataSource
import com.mospolytech.mospolyhelper.di.account.*
import com.mospolytech.mospolyhelper.di.core.coreModule
import com.mospolytech.mospolyhelper.di.deadline.deadlineModule
import com.mospolytech.mospolyhelper.di.main.mainModule
import com.mospolytech.mospolyhelper.di.relevant.relevantModule
import com.mospolytech.mospolyhelper.di.schedule.scheduleModule
import com.mospolytech.mospolyhelper.di.utilities.addresses.addressesModule
import com.mospolytech.mospolyhelper.di.utilities.news.newsModule
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
}

fun KoinApplication.androidAssetManager(assetManager: AssetManager): KoinApplication {
    if (koin.logger.isAt(Level.INFO)) {
        koin.logger.info("[init] declare Assets Provider")
    }
    koin.loadModules(listOf(module {
        single<AssetManager> { assetManager }
    }))
    return this
}

val diModules = listOf(
    appModule,
    coreModule,
    mainModule,

    // Account
    menuModule,
    authModule,
    infoModule,
    marksModule,
    studentsModule,
    teachersModule,
    classmatesModule,
    messagingModule,
    applicationsModule,
    paymentsModule,
    deadlinesModule,
    statementsModule,
    dialogsModule,
    groupMarksModule,

    // Utilities
    addressesModule,
    deadlineModule,
    newsModule,

    // Schedule
    scheduleModule,

    // Relevant
    relevantModule
)


