package com.mospolytech.mospolyhelper

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}