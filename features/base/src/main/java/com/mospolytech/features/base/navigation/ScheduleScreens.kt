package com.mospolytech.features.base.navigation

object ScheduleScreens {
    private const val prefix = "schedule"

    object Menu : Screen(
        "$prefix-menu"
    )

    object Main : Screen(
        "$prefix-main"
    )

    object Calendar : Screen(
        "$prefix-calendar"
    )

    object LessonsReview : Screen(
        "$prefix-review"
    )
}