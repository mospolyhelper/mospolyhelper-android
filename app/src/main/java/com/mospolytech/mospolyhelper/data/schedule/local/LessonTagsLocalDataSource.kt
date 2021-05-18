package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.setAsJson
import com.mospolytech.mospolyhelper.domain.core.model.ExceptionMessage
import com.mospolytech.mospolyhelper.domain.core.model.Message
import com.mospolytech.mospolyhelper.domain.core.model.SuccessMessage
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagException
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagMessages
import com.mospolytech.mospolyhelper.utils.Result2

class LessonTagsLocalDataSource(
    private val prefs: SharedPreferencesDataSource
) {
    fun getAll(): Result2<List<LessonTag>> {
        return Result2.Success(prefs.getFromJson("ScheduleTags") ?: emptyList())
    }

    fun addTag(tag: LessonTag): Result2<List<LessonTag>> {
        if (tag.title.isEmpty()) {
            return Result2.Failure(LessonTagException(LessonTagMessages.EmptyTitle))
        }
        val resTag = getAll()
        if (resTag is Result2.Success) {
            val tags = resTag.value
            if (tags.any { it.title.equals(tag.title, ignoreCase = true) }) {
                return Result2.Failure(LessonTagException(LessonTagMessages.AlreadyExist))
            }
            val newTags = (tags + tag).sorted()
            setAll(newTags)
            return Result2.Success(newTags)
        } else {
            return resTag
        }
    }

    fun addTagToLesson(tagTitle: String, lesson: LessonTagKey): Result2<List<LessonTag>> {
        val resTag = getAll()
        if (resTag is Result2.Success) {
            val tags = resTag.value
            val newTags = tags.map { tag ->
                if (tag.title.equals(tagTitle, ignoreCase = true)) {
                    tag.copy(lessons = tag.lessons + lesson)
                } else {
                    tag
                }
            }
            setAll(newTags)
            return Result2.Success(newTags)
        } else {
            return resTag
        }

    }

    fun editTag(tagTitle: String, newTitle: String, newColor: Int): Result2<List<LessonTag>> {
        if (newTitle.isEmpty()) {
            return Result2.Failure(LessonTagException(LessonTagMessages.EmptyTitle))
        }
        val resTag = getAll()
        if (resTag is Result2.Success) {
            val tags = resTag.value
            if (tags.any { it.title.equals(newTitle, ignoreCase = true) }) {
                return Result2.Failure(LessonTagException(LessonTagMessages.AlreadyExist))
            }
            val newTags = tags.map { tag ->
                if (tag.title == tagTitle) {
                    tag.copy(title = newTitle, color = newColor)
                } else {
                    tag
                }
            }
            setAll(newTags)
            return Result2.Success(newTags)
        } else {
            return resTag
        }
    }

    fun removeTag(tagTitle: String): Result2<List<LessonTag>> {
        val resTag = getAll()
        if (resTag is Result2.Success) {
            val tags = resTag.value
            val newTags = tags.filter { !it.title.equals(tagTitle, ignoreCase = true) }
            setAll(newTags)
            return Result2.Success(newTags)
        } else {
            return resTag
        }
    }

    fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey): Result2<List<LessonTag>> {
        val resTag = getAll()
        if (resTag is Result2.Success) {
            val tags = resTag.value
            val newTags = tags.map { tag ->
                if (tag.title.equals(tagTitle, ignoreCase = true)) {
                    tag.copy(lessons = tag.lessons.filter { it != lesson })
                } else {
                    tag
                }
            }
            setAll(newTags)
            return Result2.Success(newTags)
        } else {
            return resTag
        }
    }

    fun setAll(tags: List<LessonTag>) {
        prefs.setAsJson("ScheduleTags", tags)
    }
}