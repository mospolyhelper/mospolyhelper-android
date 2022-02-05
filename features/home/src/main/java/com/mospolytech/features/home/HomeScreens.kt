package com.mospolytech.features.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.mospolytech.features.base.core.navigation.addScreen
import com.mospolytech.features.base.core.navigation.getRoute
import com.mospolytech.features.base.core.navigation.groupScreen
import com.mospolytech.features.base.navigation.HomeScreens
import com.mospolytech.features.base.navigation.MainScreen

fun NavGraphBuilder.homeScreens() {
    groupScreen<MainScreen.Home, HomeScreens.Main> {
        addScreen<HomeScreens.Main> { HomeScreen() }
    }
}