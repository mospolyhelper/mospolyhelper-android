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
import com.mospolytech.features.account.teachers.TeachersScreen
import com.mospolytech.features.base.core.navigation.compose.addScreen
import com.mospolytech.features.base.core.navigation.compose.groupScreen
import com.mospolytech.features.base.navigation.AccountScreens
import com.mospolytech.features.base.navigation.MainScreen

fun NavGraphBuilder.accountScreens() {
    groupScreen<MainScreen.Account, AccountScreens.Menu> {
        addScreen<AccountScreens.Menu> { AccountMainScreen() }
        addScreen<AccountScreens.Applications> { ApplicationsScreen() }
        addScreen<AccountScreens.Authorization> { AuthScreen() }
        addScreen<AccountScreens.Payments> { PaymentsScreen() }
        addScreen<AccountScreens.Teachers> { TeachersScreen() }
        addScreen<AccountScreens.Classmates> { ClassmatesScreen() }
        addScreen<AccountScreens.Students> { StudentsScreen() }
        addScreen<AccountScreens.Marks> { MarksScreen() }
        addScreen<AccountScreens.Personal> { PersonalScreen() }
    }
}
