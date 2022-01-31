package com.mospolytech.features.base

import com.mospolytech.features.base.navigation.core.Router
import org.koin.dsl.module

val baseUiModule = module {
    single { Router() }
}
