package com.mospolytech.features.schedule

import com.mospolytech.features.schedule.lessons_review.LessonsReviewViewModel
import com.mospolytech.features.schedule.main.ScheduleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val scheduleFeaturesModule = module {
    viewModel { ScheduleViewModel(get(), get()) }
    viewModel { LessonsReviewViewModel(get()) }
}