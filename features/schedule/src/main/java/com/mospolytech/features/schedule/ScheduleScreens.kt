package com.mospolytech.features.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mospolytech.features.base.navigation.ScheduleScreens
import com.mospolytech.features.schedule.calendar.ScheduleCalendarScreen
import com.mospolytech.features.schedule.free_place.FreePlaceScreen
import com.mospolytech.features.schedule.lessons_review.LessonsReviewScreen
import com.mospolytech.features.schedule.main.ScheduleScreen
import com.mospolytech.features.schedule.menu.ScheduleMenuScreen
import com.mospolytech.features.base.navigation.MainScreen

fun NavGraphBuilder.scheduleScreens() {
    navigation(startDestination = ScheduleScreens.Menu.route, route = MainScreen.Schedule.route) {
        composable(ScheduleScreens.Menu.route) { ScheduleMenuScreen() }
        composable(ScheduleScreens.Main.route) { ScheduleScreen() }
        composable(ScheduleScreens.Calendar.route) { ScheduleCalendarScreen() }
        composable(ScheduleScreens.LessonsReview.route) { LessonsReviewScreen() }
        composable(ScheduleScreens.Source.route) {  }
        composable(ScheduleScreens.FreePlace.route) { FreePlaceScreen() }
    }
}