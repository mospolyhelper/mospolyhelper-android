package com.mospolytech.mospolyhelper

import android.app.Application
import com.mospolytech.mospolyhelper.di.account.*
import com.mospolytech.mospolyhelper.di.androidAssetManager
import com.mospolytech.mospolyhelper.di.diModules
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
            androidAssetManager(this@App.assets)
            modules(diModules)
        }
    }
}