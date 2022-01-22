package com.mospolytech.features.schedule.menu

import com.mospolytech.domain.schedule.model.lesson.Lesson
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.navigation.ScheduleScreens
import java.time.LocalDate

class ScheduleMenuViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<ScheduleMenuState, ScheduleMenuMutator, Nothing>(
    ScheduleMenuState(),
    ::ScheduleMenuMutator
) {
    fun onScheduleClick() {
        router.navigateTo(ScheduleScreens.Main)
    }

    fun onLessonsReviewClick() {
        router.navigateTo(ScheduleScreens.LessonsReview)
    }

    fun onScheduleCalendarClick() {
        router.navigateTo(ScheduleScreens.Calendar)
    }

    fun onScheduleSourceClick() {
        router.navigateTo(ScheduleScreens.Source)
    }

    fun onFreePlaceClick() {
        router.navigateTo(ScheduleScreens.FreePlace)
    }
}

class ScheduleMenuState(
    val currentLessons: List<Lesson> = emptyList(),
    val date: LocalDate = LocalDate.now()
)

class ScheduleMenuMutator : BaseMutator<ScheduleMenuState>()