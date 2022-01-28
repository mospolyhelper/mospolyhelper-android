package com.mospolytech.domain.schedule.utils

import com.mospolytech.domain.base.utils.ZoneIds
import com.mospolytech.domain.schedule.model.schedule.LessonsByTime
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

fun getClosestLessons(
    lessons: List<LessonsByTime>,
    timeToCombine: Duration = 15.toDuration(DurationUnit.MINUTES)
): List<LessonsByTime> {
    val now = ZonedDateTime.now().withZoneSameInstant(ZoneIds.main).toLocalTime()
    val latestTimeToCombine = now.plus(timeToCombine.toJavaDuration())

    var firstCeilingLessonIndex: Int = -1
    val closestLessons = mutableListOf<Int>()

    lessons.forEachIndexed { lessonToAddIndex, lessonToAdd ->
        if (now in lessonToAdd.time) {
            closestLessons.add(lessonToAddIndex)

        } else if (now < lessonToAdd.time.startTime) {
            if (firstCeilingLessonIndex == -1 ||
                lessonToAdd.time.startTime < lessons[firstCeilingLessonIndex].time.startTime) {
                firstCeilingLessonIndex = lessonToAddIndex
            }

            if (lessonToAdd.time.startTime <= latestTimeToCombine) {
                closestLessons.add(lessonToAddIndex)
            }
        }
    }

    if (firstCeilingLessonIndex != -1 && firstCeilingLessonIndex !in closestLessons) {
        closestLessons.add(firstCeilingLessonIndex)
    }


    return closestLessons.map { lessons[it] }.sortedBy { it.time }
}