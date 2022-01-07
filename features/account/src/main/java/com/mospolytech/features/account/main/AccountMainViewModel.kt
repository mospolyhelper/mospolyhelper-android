package com.mospolytech.features.account.main

import com.mospolytech.features.account.main.model.MenuUi
import com.mospolytech.features.account.main.model.MenuUi.*
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.navigation.AccountScreens
import com.mospolytech.features.base.utils.nav

class AccountMainViewModel :
    BaseViewModel<AccountMenuState, AccountMenuMutator, Nothing>(AccountMenuState(), ::AccountMenuMutator) {

    fun navigateToAuth() {
        navController.nav(AccountScreens.Authorization)
    }

    fun navigateToApplications() {
        navController.nav(AccountScreens.Applications)
    }

    fun navigateToPayments() {
        navController.nav(AccountScreens.Payments)
    }

    fun navigateToStudents() {
        navController.nav(AccountScreens.Students)
    }

    fun navigateToTeachers() {
        navController.nav(AccountScreens.Teachers)
    }

    fun navigateToClassmates() {
        navController.nav(AccountScreens.Classmates)
    }

    fun navigateToMarks() {
        navController.nav(AccountScreens.Marks)
    }

    fun navigateToPersonal() {
        navController.nav(AccountScreens.Personal)
    }
}

data class AccountMenuState(
    val menu: List<MenuUi> = listOf(Auth,
        Personal, Students, Teachers, Classmates, Payments, Applications, Marks)
)

class AccountMenuMutator : BaseMutator<AccountMenuState>() {
    fun setMenu(menu: List<MenuUi>) {
        if (state.menu != menu) {
            state = state.copy(menu = menu)
        }
    }
}
