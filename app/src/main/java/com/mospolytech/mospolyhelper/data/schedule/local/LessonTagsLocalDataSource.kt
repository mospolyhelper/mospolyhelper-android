package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.setAsJson
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey

class LessonTagsLocalDataSource(
    private val prefs: SharedPreferencesDataSource
) {
    fun getAll(): List<LessonTag> {
        return prefs.getFromJson("ScheduleTags") ?: emptyList()
    }

    fun addTag(tag: LessonTag) {
        setAll((getAll() + tag).sorted())
    }

    fun editTag(tagTitle: String, newTitle: String, newColor: Int) {
        setAll(getAll().map { tag ->
            if (tag.title == tagTitle) {
                tag.copy(title = newTitle, color = newColor)
            } else {
                tag
            }
        })
    }

    fun removeTag(tagTitle: String) {
        setAll(getAll().filter { it.title != tagTitle })
    }

    fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey) {
        setAll(getAll().map { tag ->
            if (tag.title == tagTitle) {
                tag.copy(lessons = tag.lessons.filter { it != lesson })
            } else {
                tag
            }
        })
    }

    fun setAll(tags: List<LessonTag>) {
        prefs.setAsJson("ScheduleTags", tags)
    }
}