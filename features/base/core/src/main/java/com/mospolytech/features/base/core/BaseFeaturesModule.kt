package com.mospolytech.features.base.core

import com.mospolytech.features.base.core.navigation.core.Router
import org.koin.dsl.module

val baseFeaturesModule = module {
    single { Router() }
}
