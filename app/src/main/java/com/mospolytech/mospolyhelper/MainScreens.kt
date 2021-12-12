package com.mospolytech.mospolyhelper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

sealed class MainScreen(
    val route: String,
    @DrawableRes val iconId: Int,
    @DrawableRes val iconSelectedId: Int,
    @StringRes val resourceId: Int
) {
    fun getIcon(selected: Boolean) =
        if (selected) iconSelectedId else iconId

    object Home : MainScreen(
        "home",
        R.drawable.ic_fluent_home_24_regular,
        R.drawable.ic_fluent_home_24_filled,
        R.string.menu_home
    )
    object Schedule : MainScreen(
        "history",
        R.drawable.ic_fluent_calendar_ltr_24_regular,
        R.drawable.ic_fluent_calendar_ltr_24_filled,
        R.string.menu_schedule
    )
    object Account : MainScreen(
        "payments",
        R.drawable.ic_fluent_person_24_regular,
        R.drawable.ic_fluent_person_24_filled,
        R.string.menu_account
    )
    object Misc : MainScreen(
        "misc",
        R.drawable.ic_fluent_apps_24_regular,
        R.drawable.ic_fluent_apps_24_filled,
        R.string.menu_misc
    )
}

fun NavGraphBuilder.mainScreens() {
    composable(MainScreen.Home.route) { TestScreen("Home") }
    composable(MainScreen.Schedule.route) { TestScreen("Schedule") }
    composable(MainScreen.Account.route) { TestScreen("Account") }
    composable(MainScreen.Misc.route) { TestScreen("Miscellaneous") }
}

@Composable
fun TestScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

