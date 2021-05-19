package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface LessonTagsRepository {
    fun getAll(): Flow<Result2<List<LessonTag>>>

    suspend fun addTag(tag: LessonTag)

    suspend fun addTagToLesson(tagTitle: String, lesson: LessonTagKey)

    suspend fun editTag(tagTitle: String, newTitle: String, newColor: Int)

    suspend fun removeTag(tagTitle: String)

    suspend fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey)
}