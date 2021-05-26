package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.setAsJson
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagException
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagMessages
import com.mospolytech.mospolyhelper.utils.Result2

import com.mospolytech.mospolyhelper.utils.map
import com.mospolytech.mospolyhelper.utils.mapCatching

class LessonTagsLocalDataSource(
    private val prefs: SharedPreferencesDataSource
) {
    fun getAll(): Result2<List<LessonTag>> {
        return Result2.success(prefs.getFromJson("ScheduleTags") ?: emptyList())
    }

    fun addTag(tag: LessonTag): Result2<List<LessonTag>> {
        if (tag.title.isEmpty()) {
            return Result2.failure(LessonTagException(LessonTagMessages.EmptyTitle))
        }
        return getAll().mapCatching { tags ->
            if (tags.any { it.title.equals(tag.title, ignoreCase = true) }) {
                throw LessonTagException(LessonTagMessages.AlreadyExist)
            }
            val newTags = (tags + tag).sorted()
            setAll(newTags)
            newTags
        }
    }

    fun addTagToLesson(tagTitle: String, lesson: LessonTagKey): Result2<List<LessonTag>> {
        return getAll().map { tags ->
            val newTags = tags.map { tag ->
                if (tag.title.equals(tagTitle, ignoreCase = true)) {
                    tag.copy(lessons = tag.lessons + lesson)
                } else {
                    tag
                }
            }
            setAll(newTags)
            newTags
        }
    }

    fun editTag(tagTitle: String, newTitle: String, newColor: Int): Result2<List<LessonTag>> {
        if (newTitle.isEmpty()) {
            return Result2.failure(LessonTagException(LessonTagMessages.EmptyTitle))
        }
        return getAll().mapCatching { tags ->
            if (tags.any { it.title.equals(newTitle, ignoreCase = true) }) {
                throw LessonTagException(LessonTagMessages.AlreadyExist)
            }
            val newTags = tags.map { tag ->
                if (tag.title == tagTitle) {
                    tag.copy(title = newTitle, color = newColor)
                } else {
                    tag
                }
            }
            setAll(newTags)
            newTags
        }
    }

    fun removeTag(tagTitle: String): Result2<List<LessonTag>> {
        return getAll().map { tags ->
            val newTags = tags.filter { !it.title.equals(tagTitle, ignoreCase = true) }
            setAll(newTags)
            newTags
        }
    }

    fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey): Result2<List<LessonTag>> {
        return getAll().map { tags ->
            val newTags = tags.map { tag ->
                if (tag.title.equals(tagTitle, ignoreCase = true)) {
                    tag.copy(lessons = tag.lessons.filter { it != lesson })
                } else {
                    tag
                }
            }
            setAll(newTags)
            newTags
        }
    }

    fun setAll(tags: List<LessonTag>) {
        prefs.setAsJson("ScheduleTags", tags)
    }
}