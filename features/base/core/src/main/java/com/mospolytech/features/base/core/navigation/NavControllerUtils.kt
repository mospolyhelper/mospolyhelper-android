package com.mospolytech.features.base.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.ktor.util.*

inline fun <reified T : Screen> NavGraphBuilder.addScreen(
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable (ScreenArgs) -> Unit
) {
    val fullRoute = "${getRoute<T>()}?screen={screen}"
    val navArgument = navArgument("screen") { defaultValue = "" }

    composable(fullRoute, listOf(navArgument), deepLinks) {
        val args = it.arguments?.getString("screen")
            ?.let { toBaseScreenArgs(it) } ?: ScreenArgs(emptyMap())

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

class ScreenArgs(val args: Map<String, String>)

fun BaseScreen.toUrlParameter(): String {
    return args.entries.joinToString(separator = separatorPair) {
        buildString {
            append(it.key.escape())
            append(separatorKeyValue)
            append(it.value.escape())
        }
    }.encodeBase64()
}

fun toBaseScreenArgs(url: String): ScreenArgs {
    return ScreenArgs(
        url.decodeBase64String()
            .split(separatorPair)
            .filter { it.isNotEmpty() }
            .associate {
                it.split(separatorKeyValue)
                    .run {
                        if (size == 2)
                            get(0).unescape() to get(1).unescape()
                        else
                            "" to ""
                    }
            }
    )
}

private const val escape = "+"
private const val separatorKeyValue = "="
private const val separatorPair = ","

private val regex = """([$escape$separatorKeyValue$separatorPair])""".toRegex()
private val regex2 = """\$escape([$escape$separatorKeyValue$separatorPair])""".toRegex()

private fun String.escape(): String {
    return replace(regex, """\$escape$1""")
}

private fun String.unescape(): String {
    return replace(regex2, """$1""")
}

inline fun <reified T : BaseScreen> T.getRoute() =
    T::class.qualifiedName?.replace(".", "-") ?: ""

inline fun <reified T : BaseScreen> getRoute() =
    T::class.qualifiedName?.replace(".", "-") ?: ""