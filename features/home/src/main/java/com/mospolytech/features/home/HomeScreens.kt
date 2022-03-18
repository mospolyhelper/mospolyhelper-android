package com.mospolytech.features.home

import androidx.navigation.NavGraphBuilder
import com.mospolytech.features.base.core.navigation.compose.addScreen
import com.mospolytech.features.base.core.navigation.compose.groupScreen
import com.mospolytech.features.base.navigation.HomeScreens
import com.mospolytech.features.base.navigation.MainScreen

fun NavGraphBuilder.homeScreens() {
    groupScreen<MainScreen.Home, HomeScreens.Main> {
        addScreen<HomeScreens.Main> { HomeScreen() }
    }
}