package com.mospolytech.features.schedule

import com.mospolytech.features.schedule.calendar.ScheduleCalendarViewModel
import com.mospolytech.features.schedule.free_place.FreePlaceViewModel
import com.mospolytech.features.schedule.lessons_review.LessonsReviewViewModel
import com.mospolytech.features.schedule.main.ScheduleViewModel
import com.mospolytech.features.schedule.menu.ScheduleMenuViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val scheduleFeaturesModule = module {
    viewModel { ScheduleMenuViewModel(get()) }
    viewModel { ScheduleViewModel(get()) }
    viewModel { LessonsReviewViewModel(get()) }
    viewModel { ScheduleCalendarViewModel(get()) }
    viewModel { FreePlaceViewModel(get()) }
}