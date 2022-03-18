package com.mospolytech.features.base.core.navigation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import com.mospolytech.features.base.core.navigation.core.Router

@Composable
fun rememberNavController(
    router: Router,
    vararg navigators: Navigator<out NavDestination>
): NavHostController {
    val navController = androidx.navigation.compose.rememberNavController(*navigators)
    val navigator = remember(navController, router) {
        ComposeNavigator(navController, router)
    }

    return navigator.navController
}