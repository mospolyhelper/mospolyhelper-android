package com.mospolytech.features.base.core.navigation.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.mospolytech.features.base.core.navigation.core.Screen
import com.mospolytech.features.base.core.navigation.core.ScreenInfo
import com.mospolytech.features.base.core.navigation.core.ScreenInfoSerializer
import io.ktor.util.*
import kotlin.reflect.KClass

inline fun <reified T : Screen> NavGraphBuilder.addScreen(
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable Screen.() -> Unit
) {
    val route = getRoute<T>()
    val fullRoute = "$route?screen={screen}"
    val navArgument = navArgument("screen") { defaultValue = "" }

    composable(fullRoute, listOf(navArgument), deepLinks) {
        val args = it.arguments?.getString("screen")
            ?.let { ScreenInfoSerializer.deserialize(it.decodeBase64String()) }
            ?: ScreenInfo("", emptyMap())

        content(args)
    }
}

inline fun <reified TRoute : Screen, reified TStart : Screen> NavGraphBuilder.groupScreen(
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline builder: NavGraphBuilder.() -> Unit
) {
    val startDestination = "${getRoute<TStart>()}?screen={screen}"

    navigation(startDestination, getRoute<TRoute>(), arguments, deepLinks, builder)
}



fun Screen.getRoute() =
    this.key.replace(".", "-")

fun Screen.getUrlArgs() =
    ScreenInfoSerializer.serialize(this).encodeBase64()

fun Screen.getFullRoute() =
    "${getRoute()}?screen=${getUrlArgs()}"


inline fun <reified T : Screen> getRoute() =
    T::class.qualifiedName?.replace(".", "-") ?: ""

fun <T : Screen> KClass<T>.getRoute() =
    qualifiedName?.replace(".", "-") ?: ""