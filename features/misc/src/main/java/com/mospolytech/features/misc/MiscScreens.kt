package com.mospolytech.features.misc

import androidx.navigation.NavGraphBuilder
import com.mospolytech.features.base.core.navigation.compose.addScreen
import com.mospolytech.features.base.core.navigation.compose.groupScreen
import com.mospolytech.features.base.navigation.MiscScreens
import com.mospolytech.features.base.navigation.MainScreen

fun NavGraphBuilder.miscScreens() {
    groupScreen<MainScreen.Misc, MiscScreens.Menu> {
        addScreen<MiscScreens.Menu> { MiscMenuScreen() }
    }
}