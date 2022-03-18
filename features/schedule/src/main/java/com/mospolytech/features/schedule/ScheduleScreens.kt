package com.mospolytech.features.schedule

import androidx.navigation.NavGraphBuilder
import com.mospolytech.features.base.core.navigation.compose.addScreen
import com.mospolytech.features.base.core.navigation.compose.groupScreen
import com.mospolytech.features.base.navigation.ScheduleScreens
import com.mospolytech.features.schedule.calendar.ScheduleCalendarScreen
import com.mospolytech.features.schedule.free_place.FreePlaceScreen
import com.mospolytech.features.schedule.lessons_review.LessonsReviewScreen
import com.mospolytech.features.schedule.main.ScheduleScreen
import com.mospolytech.features.schedule.menu.ScheduleMenuScreen
import com.mospolytech.features.base.navigation.MainScreen
import com.mospolytech.features.schedule.sources.ScheduleSourcesScreen

fun NavGraphBuilder.scheduleScreens() {
    groupScreen<MainScreen.Schedule, ScheduleScreens.Menu> {
        addScreen<ScheduleScreens.Menu> { ScheduleMenuScreen() }
        addScreen<ScheduleScreens.Main> { ScheduleScreen() }
        addScreen<ScheduleScreens.Calendar> { ScheduleCalendarScreen() }
        addScreen<ScheduleScreens.LessonsReview> { LessonsReviewScreen() }
        addScreen<ScheduleScreens.Source> { ScheduleSourcesScreen() }
        addScreen<ScheduleScreens.FreePlace> { FreePlaceScreen() }
    }
}