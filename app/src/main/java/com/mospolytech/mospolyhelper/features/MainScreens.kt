package com.mospolytech.mospolyhelper.features

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
import com.mospolytech.features.base.navigation.HomeScreens
import com.mospolytech.features.base.navigation.MiscScreens
import com.mospolytech.features.base.navigation.ScheduleScreens
import com.mospolytech.features.schedule.main.ScheduleScreen
import com.mospolytech.mospolyhelper.R

sealed class MainScreen(
    val route: String,
    @DrawableRes val iconId: Int,
    @DrawableRes val iconSelectedId: Int,
    @StringRes val resourceId: Int
) {
    fun getIcon(selected: Boolean) =
        if (selected) iconSelectedId else iconId

    object Home : MainScreen(
        HomeScreens.Main.route,
        R.drawable.ic_fluent_home_24_regular,
        R.drawable.ic_fluent_home_24_filled,
        R.string.menu_home
    )
    object Schedule : MainScreen(
        ScheduleScreens.Menu.route,
        R.drawable.ic_fluent_calendar_ltr_24_regular,
        R.drawable.ic_fluent_calendar_ltr_24_filled,
        R.string.menu_schedule
    )
    object Account : MainScreen(
        "account",
        R.drawable.ic_fluent_person_24_regular,
        R.drawable.ic_fluent_person_24_filled,
        R.string.menu_account
    )
    object Misc : MainScreen(
        MiscScreens.Menu.route,
        R.drawable.ic_fluent_apps_24_regular,
        R.drawable.ic_fluent_apps_24_filled,
        R.string.menu_misc
    )
}

