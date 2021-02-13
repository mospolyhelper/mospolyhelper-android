package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.Tag
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TagLocalDataSource(
    private val prefs: SharedPreferencesDataSource
) {
    fun getAll(): Map<LessonTagKey, List<Tag>> {
        return try {
            val json = prefs.getString("ScheduleTags", "")
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun setAll(tags: Map<LessonTagKey, List<Tag>>) {
        try {
            val json = Json.encodeToString(tags)
            prefs.setString("ScheduleTags", json)
        } catch (e: Exception) {
        }
    }
}