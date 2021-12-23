package com.mospolytech.features.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mospolytech.features.account.main.AccountMainScreen
import com.mospolytech.features.base.navigation.AccountScreens

fun NavGraphBuilder.accountScreens() {
    composable(AccountScreens.Menu.route) { AccountMainScreen() }
}