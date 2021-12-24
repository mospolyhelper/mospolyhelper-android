package com.mospolytech.features.account

import androidx.navigation.NavGraphBuilder
import com.mospolytech.features.account.applications.ApplicationsScreen
import com.mospolytech.features.account.authorization.AuthScreen
import com.mospolytech.features.account.classmates.ClassmatesScreen
import com.mospolytech.features.account.main.AccountMainScreen
import com.mospolytech.features.account.marks.MarksScreen
import com.mospolytech.features.account.payments.PaymentsScreen
import com.mospolytech.features.account.personal.PersonalScreen
import com.mospolytech.features.account.students.StudentsScreen
import com.mospolytech.features.base.navigation.*
import com.mospolytech.features.base.utils.composable

fun NavGraphBuilder.accountScreens() {
    composable(AccountScreens.Menu) { AccountMainScreen() }
    composable(AccountScreens.Applications) { ApplicationsScreen() }
    composable(AccountScreens.Authorization) { AuthScreen() }
    composable(AccountScreens.Payments) { PaymentsScreen() }
    composable(AccountScreens.Teachers) { }
    composable(AccountScreens.Classmates) { ClassmatesScreen() }
    composable(AccountScreens.Students) { StudentsScreen() }
    composable(AccountScreens.Marks) { MarksScreen() }
    composable(AccountScreens.Personal) { PersonalScreen() }
}
