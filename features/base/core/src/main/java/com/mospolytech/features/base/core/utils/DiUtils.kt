package com.mospolytech.features.base.core.utils

import android.content.Context
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.dsl.module

fun KoinApplication.androidSharedPreferences(androidContext: Context, preferencesName: String): KoinApplication {
    if (koin.logger.isAt(Level.INFO)) {
        koin.logger.info("[init] declare SharedPreferences")
    }

    koin.loadModules(
        listOf(
            module {
                single { androidContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE) }
            }
        )
    )

    return this
}
