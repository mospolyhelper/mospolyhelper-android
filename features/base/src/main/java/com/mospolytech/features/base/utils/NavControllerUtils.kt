package com.mospolytech.features.base.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.composable
import com.mospolytech.features.base.navigation.core.Screen

//fun createNavController(context: Context) =
//    NavHostController(context).apply {
//        navigatorProvider.addNavigator(ComposeNavigator())
//        navigatorProvider.addNavigator(DialogNavigator())
//    }
//
//fun NavController.nav(screen: Screen) = navigate(screen.route)

fun NavGraphBuilder.composable(
    screen: Screen,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(screen.route, arguments, deepLinks, content)
}