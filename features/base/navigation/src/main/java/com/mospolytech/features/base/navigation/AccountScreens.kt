package com.mospolytech.features.base.navigation

import com.mospolytech.features.base.core.navigation.Screen

object AccountScreens {

    private const val prefix = "account"

    object Menu : Screen()//"$prefix-menu")

    object Authorization : Screen()//"$prefix-auth")

    object Applications : Screen()//"$prefix-applications")

    object Personal : Screen()//"$prefix-personal")

    object Marks : Screen()//"$prefix-marks")

    object Students : Screen()//"$prefix-students")

    object Classmates : Screen()//"$prefix-classmates")

    object Teachers : Screen()//"$prefix-teachers")

    object Payments : Screen()//"$prefix-payments")
}