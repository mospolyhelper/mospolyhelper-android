package com.mospolytech.mospolyhelper

import androidx.test.core.app.ApplicationProvider
import com.mospolytech.mospolyhelper.di.addresses.addressesModule
import com.mospolytech.mospolyhelper.di.appModule
import com.mospolytech.mospolyhelper.di.core.coreModule
import com.mospolytech.mospolyhelper.di.deadline.deadlineModule
import com.mospolytech.mospolyhelper.di.main.mainModule
import com.mospolytech.mospolyhelper.di.schedule.scheduleModule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Category(CheckModuleTest::class)
class KoinModulesTest : KoinTest {

    @Test
    fun checkAllModules() {
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())

            modules(
                appModule,
                coreModule,
                mainModule,

                addressesModule,
                deadlineModule,
                scheduleModule
            )
        }.checkModules()
    }

}