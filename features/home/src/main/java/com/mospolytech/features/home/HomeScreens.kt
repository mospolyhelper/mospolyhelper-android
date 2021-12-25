package com.mospolytech.features.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mospolytech.features.base.navigation.HomeScreens
import com.mospolytech.features.base.navigation.MainScreen

fun NavGraphBuilder.homeScreens() {
    navigation(startDestination = HomeScreens.Main.route, route = MainScreen.Home.route) {
        composable(HomeScreens.Main.route) { HomeScreen() }
    }
}