package com.mospolytech.features.schedule.menu

import com.mospolytech.domain.schedule.model.lesson.Lesson
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.navigation.ScheduleScreens
import com.mospolytech.features.base.navigation.*
import com.mospolytech.features.base.utils.nav
import java.time.LocalDate

class ScheduleMenuViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<ScheduleMenuState, ScheduleMenuMutator, Nothing>(
    ScheduleMenuState(),
    ::ScheduleMenuMutator
) {
    fun onScheduleClick() {
        navController.nav(ScheduleScreens.Main)
    }

    fun onLessonsReviewClick() {
        navController.navigate(ScheduleScreens.LessonsReview.route)
    }

    fun onScheduleCalendarClick() {
        navController.navigate(ScheduleScreens.Calendar.route)
    }

    fun onScheduleSourceClick() {
        navController.navigate(ScheduleScreens.Source.route)
    }

    fun onFreePlaceClick() {
        navController.navigate(ScheduleScreens.FreePlace.route)
    }
}

class ScheduleMenuState(
    val currentLessons: List<Lesson> = emptyList(),
    val date: LocalDate = LocalDate.now()
)

class ScheduleMenuMutator : BaseMutator<ScheduleMenuState>()