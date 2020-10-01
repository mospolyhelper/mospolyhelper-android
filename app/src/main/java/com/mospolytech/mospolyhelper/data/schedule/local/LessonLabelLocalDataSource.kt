package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.domain.schedule.model.LessonLabelKey

class LessonLabelLocalDataSource {
    fun get(lessonLabelKeyKey: LessonLabelKey): Set<String> {
        return if (lessonLabelKeyKey.lessonDate == null) {
            setOf("#Важное")
        } else {
            setOf("#Неважное", "#Сдано")
        }
    }

    fun getAll(): Map<LessonLabelKey, Set<String>> {
        return  mapOf()
    }

    fun remove(label: String, lessonLabelKeyKey: LessonLabelKey) {

    }

    fun add(label: String, lessonLabelKeyKey: LessonLabelKey) {

    }

    fun getAllLabels(): Set<String> {
        return setOf("#Важное2", "#Неважное2", "#Можно_пропустить2", "#Сдано2") +
                setOf("#Важное", "#Неважное", "#Можно_пропустить", "#Сдано")
    }
}