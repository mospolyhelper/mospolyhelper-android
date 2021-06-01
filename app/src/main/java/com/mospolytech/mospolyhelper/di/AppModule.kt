package com.mospolytech.mospolyhelper.di

import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.data.core.local.AssetsDataSource
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
    single { AssetsDataSource(get()) }
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


