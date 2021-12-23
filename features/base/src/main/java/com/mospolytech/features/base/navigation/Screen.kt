package com.mospolytech.features.base.navigation

import androidx.navigation.NavController

open class Screen(
    val route: String,
    val args: List<ScreenArg> = emptyList()
)

class ScreenArg(
    val name: String,
    val content: Any
)

fun Screen.navigate(navManager: NavController) {
    navManager.navigate(route)
}