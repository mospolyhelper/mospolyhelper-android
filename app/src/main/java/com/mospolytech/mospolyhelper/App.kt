package com.mospolytech.mospolyhelper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.AssetManager
import android.os.Build
import com.mospolytech.mospolyhelper.di.account.*
import com.mospolytech.mospolyhelper.di.addresses.addressesModule
import com.mospolytech.mospolyhelper.di.appModule
import com.mospolytech.mospolyhelper.di.core.coreModule
import com.mospolytech.mospolyhelper.di.deadline.deadlineModule
import com.mospolytech.mospolyhelper.di.main.mainModule
import com.mospolytech.mospolyhelper.di.relevant.relevantModule
import com.mospolytech.mospolyhelper.di.schedule.scheduleModule
import com.mospolytech.mospolyhelper.utils.NotificationChannelIds
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.io.InputStream

class App : Application() {
    companion object {
        var context: Context? = null

        private lateinit var assets: AssetManager

        fun getAsset(assetName: String): InputStream? {
            return try {
                assets.open(assetName)
            } catch (e: Exception ) {
                null
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val modules = listOf(
            appModule,
            coreModule,
            mainModule,

            // Account
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

            relevantModule,
            addressesModule,
            deadlineModule,
            scheduleModule
        )

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)

            modules(modules)
        }

        context = applicationContext
        App.assets = assets



        //createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_schedule_current_lesson)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NotificationChannelIds.SCHEDULE_CURRENT_LESSON, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}