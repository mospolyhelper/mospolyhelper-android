package com.mospolytech.features.schedule

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mospolytech.features.base.navigation.ScheduleScreens

fun NavGraphBuilder.scheduleScreens() {
    composable(ScheduleScreens.Main.route) { ScheduleScreen() }
    composable(ScheduleScreens.Calendar.route) {  }
    composable(ScheduleScreens.Review.route) {  }
}