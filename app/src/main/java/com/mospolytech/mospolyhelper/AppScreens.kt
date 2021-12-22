package com.mospolytech.mospolyhelper

import androidx.navigation.NavGraphBuilder
import com.mospolytech.features.home.homeScreens
import com.mospolytech.features.misc.miscScreens
import com.mospolytech.features.schedule.scheduleScreens

fun NavGraphBuilder.appScreens() {
    homeScreens()
    scheduleScreens()
    miscScreens()
}