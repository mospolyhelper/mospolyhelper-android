package com.mospolytech.mospolyhelper

import android.app.Application
import com.mospolytech.features.base.utils.androidSharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            androidSharedPreferences(this@App, BuildConfig.APPLICATION_ID)
            modules(koinModules)
        }
    }
}