package com.mospolytech.features.base.navigation.core

open class Screen(
    val route: String,
    val args: List<ScreenArg> = emptyList()
)

class ScreenArg(
    val name: String,
    val content: Any
)