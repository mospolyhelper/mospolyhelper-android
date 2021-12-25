package com.mospolytech.features.misc

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mospolytech.features.base.navigation.MiscScreens
import com.mospolytech.features.base.navigation.MainScreen

fun NavGraphBuilder.miscScreens() {
    navigation(startDestination = MiscScreens.Menu.route, route = MainScreen.Misc.route) {
        composable(MiscScreens.Menu.route) { MiscMenuScreen() }
    }
}