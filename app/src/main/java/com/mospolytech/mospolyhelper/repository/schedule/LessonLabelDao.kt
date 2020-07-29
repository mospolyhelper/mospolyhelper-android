package com.mospolytech.mospolyhelper.repository.schedule

import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.repository.schedule.models.LessonLabelKey
import java.time.LocalDate

class LessonLabelDao {
    fun get(lessonLabelKeyKey: LessonLabelKey): Set<String> {
        if (lessonLabelKeyKey.lessonDate == null) {
            return setOf("#Важное")
        } else {
            return setOf("#Неважное", "#Сдано")
        }
    }

    fun remove(label: String, lessonLabelKeyKey: LessonLabelKey) {

    }

    fun add(label: String, lessonLabelKeyKey: LessonLabelKey) {

    }

    fun getAll(): Set<String> {
        return setOf("#Важное2", "#Неважное2", "#Можно_пропустить2", "#Сдано2") +
                setOf("#Важное", "#Неважное", "#Можно_пропустить", "#Сдано")
    }
}