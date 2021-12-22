package com.mospolytech.features.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mospolytech.features.base.navigation.HomeScreens

fun NavGraphBuilder.homeScreens() {
    composable(HomeScreens.Main.route) { HomeScreen()}
}