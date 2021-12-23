package com.mospolytech.features.account.main

import androidx.navigation.NavController
import com.mospolytech.features.account.main.model.MenuUi
import com.mospolytech.features.account.main.model.MenuUi.*
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.State
import com.mospolytech.features.base.navigation.AccountScreens
import com.mospolytech.features.base.utils.nav

class AccountMainViewModel(
    override val navController: NavController
): BaseViewModel<AccountState>(AccountState(), navController) {

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

data class AccountState(
    val menu: List<MenuUi> = listOf(MenuUi.Auth,
        Personal, Students, Teachers, Classmates, Payments, Applications, Marks)
) : State<AccountState.Mutator> {
    inner class Mutator : BaseMutator<AccountState>(this) {
        fun setMenu(menu: List<MenuUi>) {
            if (state.menu != menu) {
                state = state.copy(menu = menu)
            }
        }
    }
    override fun mutator() = Mutator()
}