package com.mospolytech.mospolyhelper

import android.app.Application
import com.mospolytech.mospolyhelper.di.account.*
import com.mospolytech.mospolyhelper.di.androidAssetManager
import com.mospolytech.mospolyhelper.di.appModule
import com.mospolytech.mospolyhelper.di.core.coreModule
import com.mospolytech.mospolyhelper.di.deadline.deadlineModule
import com.mospolytech.mospolyhelper.di.diModules
import com.mospolytech.mospolyhelper.di.main.mainModule
import com.mospolytech.mospolyhelper.di.relevant.relevantModule
import com.mospolytech.mospolyhelper.di.schedule.scheduleModule
import com.mospolytech.mospolyhelper.di.utilities.addresses.addressesModule
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