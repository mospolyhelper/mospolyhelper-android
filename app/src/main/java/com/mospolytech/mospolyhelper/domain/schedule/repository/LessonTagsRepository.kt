package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.core.model.Message
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagMessages
import kotlinx.coroutines.flow.Flow

interface LessonTagsRepository {
    fun getMessage(): Flow<Message<LessonTagMessages>>

    fun getAll(): Flow<List<LessonTag>>

    suspend fun addTag(tag: LessonTag)

    suspend fun editTag(tagTitle: String, newTitle: String, newColor: Int)

    suspend fun removeTag(tagTitle: String)

    suspend fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey)
}