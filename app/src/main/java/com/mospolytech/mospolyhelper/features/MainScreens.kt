package com.mospolytech.mospolyhelper.features

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mospolytech.features.base.navigation.AccountScreens
import com.mospolytech.features.base.navigation.HomeScreens
import com.mospolytech.features.base.navigation.MiscScreens
import com.mospolytech.features.base.navigation.ScheduleScreens
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
        AccountScreens.Menu.route,
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

