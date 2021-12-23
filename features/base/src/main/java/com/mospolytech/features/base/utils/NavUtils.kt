package com.mospolytech.features.base.utils

import androidx.navigation.NavController
import com.mospolytech.features.base.navigation.Screen

fun NavController.nav(screen: Screen) = navigate(screen.route)