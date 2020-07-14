package com.mospolytech.mospolyhelper

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import java.io.InputStream

class App : Application() {
    companion object {
        lateinit var context: Context

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
        context = applicationContext
        App.assets = assets
    }
}