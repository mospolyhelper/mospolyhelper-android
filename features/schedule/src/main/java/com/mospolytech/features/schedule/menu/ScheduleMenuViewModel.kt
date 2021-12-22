package com.mospolytech.features.schedule.menu

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.navigation.ScheduleScreens

class ScheduleMenuViewModel(
    private val useCase: ScheduleUseCase,
    private val navController: NavController
) : ViewModel() {
    fun onScheduleClick() {
        navController.navigate(ScheduleScreens.Main.route)
    }

    fun onLessonsReviewClick() {
        navController.navigate(ScheduleScreens.LessonsReview.route)
    }

    fun onCalendar() {
        navController.navigate(ScheduleScreens.Calendar.route)
    }
}