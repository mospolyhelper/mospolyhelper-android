package com.mospolytech.features.base.navigation

import com.mospolytech.features.base.core.navigation.core.Screen

object ScheduleScreens {

    object Menu : Screen()

    object Main : Screen()

    object Source : Screen()

    object Calendar : Screen()

    object LessonsReview : Screen()

    object FreePlace : Screen()

    class LessonInfo(
        val lessonInfo: com.mospolytech.domain.schedule.model.lesson.LessonInfo
    ) : Screen(
        LessonInfo::lessonInfo.name to lessonInfo.serialized()
    )
}