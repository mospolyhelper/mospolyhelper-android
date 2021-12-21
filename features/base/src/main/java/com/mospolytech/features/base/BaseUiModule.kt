package com.mospolytech.features.base


import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.mospolytech.data.base.PreferencesDataSource
import com.mospolytech.features.base.utils.createNavController
import org.koin.dsl.bind
import org.koin.dsl.module

val baseUiModule = module {
    single<NavHostController> { createNavController(get()) } bind NavController::class
    single<PreferencesDataSource> { SharedPreferencesDataSource(get()) }
}
