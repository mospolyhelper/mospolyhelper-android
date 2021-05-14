package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.setAsJson
import com.mospolytech.mospolyhelper.domain.core.model.ExceptionMessage
import com.mospolytech.mospolyhelper.domain.core.model.Message
import com.mospolytech.mospolyhelper.domain.core.model.SuccessMessage
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagMessages

class LessonTagsLocalDataSource(
    private val prefs: SharedPreferencesDataSource
) {
    fun getAll(): List<LessonTag> {
        return prefs.getFromJson("ScheduleTags") ?: emptyList()
    }

    fun addTag(tag: LessonTag): Message<LessonTagMessages> {
        if (tag.title.isEmpty()) {
            return ExceptionMessage(LessonTagMessages.EmptyTitle)
        }
        val tags = getAll()
        if (tags.any { it.title.equals(tag.title, ignoreCase = true) }) {
            return ExceptionMessage(LessonTagMessages.AlreadyExist)
        }
        setAll((getAll() + tag).sorted())
        return SuccessMessage(LessonTagMessages.SuccessfulCreation)
    }

    fun addTagToLesson(tagTitle: String, lesson: LessonTagKey): Message<LessonTagMessages> {
        setAll(getAll().map { tag ->
            if (tag.title.equals(tagTitle, ignoreCase = true)) {
                tag.copy(lessons = tag.lessons + lesson)
            } else {
                tag
            }
        })
        return SuccessMessage(LessonTagMessages.SuccessfulAdding)
    }

    fun editTag(tagTitle: String, newTitle: String, newColor: Int): Message<LessonTagMessages> {
        if (newTitle.isEmpty()) {
            return ExceptionMessage(LessonTagMessages.EmptyTitle)
        }
        val tags = getAll()
        if (tags.any { it.title.equals(newTitle, ignoreCase = true) }) {
            return ExceptionMessage(LessonTagMessages.AlreadyExist)
        }
        setAll(tags.map { tag ->
            if (tag.title == tagTitle) {
                tag.copy(title = newTitle, color = newColor)
            } else {
                tag
            }
        })
        return SuccessMessage(LessonTagMessages.SuccessfulEditing)
    }

    fun removeTag(tagTitle: String): Message<LessonTagMessages> {
        setAll(getAll().filter { !it.title.equals(tagTitle, ignoreCase = true) })
        return SuccessMessage(LessonTagMessages.SuccessfulRemoving)
    }

    fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey): Message<LessonTagMessages> {
        setAll(getAll().map { tag ->
            if (tag.title.equals(tagTitle, ignoreCase = true)) {
                tag.copy(lessons = tag.lessons.filter { it != lesson })
            } else {
                tag
            }
        })
        return SuccessMessage(LessonTagMessages.SuccessfulRemoving)
    }

    fun setAll(tags: List<LessonTag>) {
        prefs.setAsJson("ScheduleTags", tags)
    }
}