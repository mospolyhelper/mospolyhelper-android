package com.mospolytech.mospolyhelper.features.ui.schedule.model

import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.utils.ScheduleUtils
import java.time.DayOfWeek
import java.time.LocalDate

class DailySchedulePack(
    val lessons: List<ScheduleItemPacked>,
    val date: LocalDate,
    val lessonFeaturesSettings: LessonFeaturesSettings
) {
    class Builder {
        private var showEmptyLessons = false
        private var showLessonWindows = false

        fun withEmptyLessons(showEmptyLessons: Boolean): Builder {
            this.showEmptyLessons = showEmptyLessons
            return this
        }

        fun withLessonWindows(showLessonWindows: Boolean): Builder {
            this.showLessonWindows = showLessonWindows
            return this
        }

        fun build(
            schedule: Schedule,
            date: LocalDate,
            dateFilter: LessonDateFilter,
            featuresSettings: LessonFeaturesSettings,
            lessonTagProvider: (lesson: Lesson, dayOfWeek: DayOfWeek, order: Int) -> List<LessonTag>,
            lessonDeadlineProvider: (lesson: Lesson) -> List<Deadline>
        ): DailySchedulePack {
            var rawDailySchedule = schedule.getLessons(
                date,
                dateFilter
            )
            if (showEmptyLessons) rawDailySchedule =
                ScheduleUtils.getEmptyPairsDecorator(rawDailySchedule)
            val dailySchedule: List<ScheduleItem> = if (showLessonWindows) {
                ScheduleUtils.getWindowsDecorator(rawDailySchedule)
            } else {
                rawDailySchedule
            }
            return DailySchedulePack(
                dailySchedule.flatMap { scheduleItem ->
                    return@flatMap when (scheduleItem) {
                        is LessonPlace -> listOf<ScheduleItemPacked>(LessonPlacePack(scheduleItem)) +
                                scheduleItem.lessons.map {
                                    LessonPack(
                                        it,
                                        scheduleItem.time,
                                        lessonTagProvider(it, date.dayOfWeek, scheduleItem.time.order),
                                        lessonDeadlineProvider(it),
                                        dateFilter,
                                        featuresSettings,
                                        date in it.dateFrom..it.dateTo
                                    )
                                }
                        is LessonWindow -> listOf<ScheduleItemPacked>(LessonWindowPack(scheduleItem))
                        else -> emptyList()
                    }
                },
                date,
                featuresSettings
            )
        }
    }
}