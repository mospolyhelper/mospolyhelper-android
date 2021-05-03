package com.mospolytech.mospolyhelper.domain.schedule.utils

import android.util.Log
import com.mospolytech.mospolyhelper.utils.TAG
import java.time.LocalTime

object LessonTimeUtils {
    val firstPair = Pair(
        LocalTime.of(9, 0),
        LocalTime.of(10, 30)
    )
    val secondPair = Pair(
        LocalTime.of(10, 40),
        LocalTime.of(12, 10)
    )

    val thirdPair = Pair(
        LocalTime.of(12, 20),
        LocalTime.of(13, 50)
    )

    val fourthPair = Pair(
        LocalTime.of(14, 30),
        LocalTime.of(16, 0)
    )

    val fifthPair = Pair(
        LocalTime.of(16, 10),
        LocalTime.of(17, 40)
    )

    val sixthPair = Pair(
        LocalTime.of(17, 50),
        LocalTime.of(19, 20)
    )

    val sixthPairEvening = Pair(
        LocalTime.of(18, 20),
        LocalTime.of(19, 40)
    )

    val seventhPair = Pair(
        LocalTime.of(19, 30),
        LocalTime.of(21, 0)
    )

    val seventhPairEvening = Pair(
        LocalTime.of(19, 50),
        LocalTime.of(21, 10)
    )

    val firstPairStr = "9:00" to "10:30"
    val secondPairStr = "10:40" to "12:10"
    val thirdPairStr = "12:20" to "13:50"
    val fourthPairStr = "14:30" to "16:00"
    val fifthPairStr = "16:10" to "17:40"
    val sixthPairStr = "17:50" to "19:20"
    val sixthPairEveningStr = "18:20" to "19:40"
    val seventhPairStr = "19:30" to "21:00"
    val seventhPairEveningStr = "19:50" to "21:10"

//    fun getOrder(time: LocalTime, groupIsEvening: Boolean): Lesson.CurrentLesson =
//        if (time > thirdPair.second) when {
//            time <= fourthPair.second -> Lesson.CurrentLesson(
//                3,
//                time >= fourthPair.first,
//                groupIsEvening
//            )
//            time <= fifthPair.second -> Lesson.CurrentLesson(
//                4,
//                time >= fifthPair.first,
//                groupIsEvening
//            )
//            groupIsEvening -> when {
//                time <= sixthPairEvening.second -> Lesson.CurrentLesson(
//                    5,
//                    time >= sixthPairEvening.first,
//                    groupIsEvening
//                )
//                time <= seventhPairEvening.second -> Lesson.CurrentLesson(
//                    6,
//                    time >= seventhPairEvening.first,
//                    groupIsEvening
//                )
//                else -> Lesson.CurrentLesson(8, false, groupIsEvening)
//            }
//            else -> when {
//                time <=  sixthPair.second -> Lesson.CurrentLesson(
//                    5,
//                    time >= sixthPair.first,
//                    groupIsEvening
//                )
//                time <=  seventhPair.second -> Lesson.CurrentLesson(
//                    6,
//                    time >= seventhPair.first,
//                    groupIsEvening
//                )
//                else -> Lesson.CurrentLesson(8, false, groupIsEvening)
//            }
//        }
//        else when {
//            time >  secondPair.second -> Lesson.CurrentLesson(
//                2,
//                time >= thirdPair.first,
//                groupIsEvening
//            )
//            time >  firstPair.second -> Lesson.CurrentLesson(
//                1,
//                time >= secondPair.first,
//                groupIsEvening
//            )
//            else -> Lesson.CurrentLesson(0, time >=  firstPair.first, groupIsEvening)
//        }

    fun getTime(order: Int, groupIsEvening: Boolean) = when (order) {
        0 -> firstPairStr
        1 -> secondPairStr
        2 -> thirdPairStr
        3 -> fourthPairStr
        4 -> fifthPairStr
        5 -> if (groupIsEvening) sixthPairEveningStr else sixthPairStr
        6 -> if (groupIsEvening) seventhPairEveningStr else seventhPairStr
        else -> {
            Log.e(TAG, "Wrong order number of lesson")
            Pair("Ошибка", "номера занятия")
        }
    }

    fun getLocalTime(order: Int, groupIsEvening: Boolean) = when (order) {
        0 -> firstPair
        1 -> secondPair
        2 -> thirdPair
        3 -> fourthPair
        4 -> fifthPair
        5 -> if (groupIsEvening) sixthPairEvening else sixthPair
        6 -> if (groupIsEvening) seventhPairEvening else seventhPair
        else -> {
            Log.e(TAG, "Wrong order number of lesson")
            Pair(LocalTime.MIN, LocalTime.MAX)
        }
    }
}