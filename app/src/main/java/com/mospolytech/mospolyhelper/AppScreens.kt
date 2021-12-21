package com.mospolytech.mospolyhelper

import androidx.navigation.NavGraphBuilder
import com.mospolytech.features.schedule.scheduleScreens
import com.mospolytech.mospolyhelper.features.mainScreens

fun NavGraphBuilder.appScreens() {
    mainScreens()
    scheduleScreens()
}