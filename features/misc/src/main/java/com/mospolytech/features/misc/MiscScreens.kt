package com.mospolytech.features.misc

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mospolytech.features.base.navigation.MiscScreens

fun NavGraphBuilder.miscScreens() {
    composable(MiscScreens.Menu.route) { MiscMenuScreen() }
}