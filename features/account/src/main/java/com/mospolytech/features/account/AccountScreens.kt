package com.mospolytech.features.account

import androidx.navigation.NavGraphBuilder
import com.mospolytech.features.account.applications.ApplicationsScreen
import com.mospolytech.features.account.main.AccountMainScreen
import com.mospolytech.features.base.navigation.*
import com.mospolytech.features.base.utils.composable

fun NavGraphBuilder.accountScreens() {
    composable(AccountScreens.Menu) { AccountMainScreen() }
    composable(AccountScreens.Applications) { ApplicationsScreen() }
    composable(AccountScreens.Authorization) { }
    composable(AccountScreens.Payments) { }
    composable(AccountScreens.Teachers) { }
    composable(AccountScreens.Classmates) { }
    composable(AccountScreens.Students) { }
    composable(AccountScreens.Marks) { }
}
