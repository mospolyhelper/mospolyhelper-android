package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.LessonTagsLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.repository.LessonTagsRepository
import com.mospolytech.mospolyhelper.utils.Result2

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class LessonTagsRepositoryImpl(
    private val dataSource: LessonTagsLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : LessonTagsRepository {

    private val changesFlow = MutableSharedFlow<Result2<List<LessonTag>>>(extraBufferCapacity = 64)

    override fun getAll() = flow {
        emit(dataSource.getAll())
        emitAll(changesFlow)
    }.flowOn(ioDispatcher)

    override suspend fun addTag(tag: LessonTag) {
        withContext(ioDispatcher) {
            changesFlow.emit(dataSource.addTag(tag))
        }
    }

    override suspend fun addTagToLesson(tagTitle: String, lesson: LessonTagKey) {
        withContext(ioDispatcher) {
            changesFlow.emit(dataSource.addTagToLesson(tagTitle, lesson))
        }
    }

    override suspend fun editTag(tagTitle: String, newTitle: String, newColor: Int) {
        withContext(ioDispatcher) {
            changesFlow.emit(dataSource.editTag(tagTitle, newTitle, newColor))
        }
    }

    override suspend fun removeTag(tagTitle: String) {
        withContext(ioDispatcher) {
            changesFlow.emit(dataSource.removeTag(tagTitle))
        }
    }

    override suspend fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey) {
        withContext(ioDispatcher) {
            changesFlow.emit(dataSource.removeTagFromLesson(tagTitle, lesson))
        }
    }
}