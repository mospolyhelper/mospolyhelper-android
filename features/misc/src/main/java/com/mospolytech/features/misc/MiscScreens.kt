package com.mospolytech.features.misc

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.mospolytech.features.base.core.navigation.addScreen
import com.mospolytech.features.base.core.navigation.getRoute
import com.mospolytech.features.base.core.navigation.groupScreen
import com.mospolytech.features.base.navigation.MiscScreens
import com.mospolytech.features.base.navigation.MainScreen

fun NavGraphBuilder.miscScreens() {
    groupScreen<MainScreen.Misc, MiscScreens.Menu> {
        addScreen<MiscScreens.Menu> { MiscMenuScreen() }
    }
}