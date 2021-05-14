package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.LessonTagsLocalDataSource
import com.mospolytech.mospolyhelper.domain.core.model.ExceptionMessage
import com.mospolytech.mospolyhelper.domain.core.model.Message
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagMessages
import com.mospolytech.mospolyhelper.domain.schedule.repository.LessonTagsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class LessonTagsRepositoryImpl(
    private val dataSource: LessonTagsLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : LessonTagsRepository {

    private val changesFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 64)
    private val messageFlow = MutableSharedFlow<Message<LessonTagMessages>>(extraBufferCapacity = 64)

    override fun getMessage(): Flow<Message<LessonTagMessages>> = messageFlow

    override fun getAll() = flow {
        emit(dataSource.getAll())
        emitAll(changesFlow.map { dataSource.getAll() })
    }.flowOn(ioDispatcher)

    override suspend fun addTag(tag: LessonTag) {
        withContext(ioDispatcher) {
            val message = dataSource.addTag(tag)
            messageFlow.emit(message)
            if (message !is ExceptionMessage) {
                changesFlow.emit(Unit)
            }
        }
    }

    override suspend fun addTagToLesson(tagTitle: String, lesson: LessonTagKey) {
        withContext(ioDispatcher) {
            val message = dataSource.addTagToLesson(tagTitle, lesson)
            messageFlow.emit(message)
            if (message !is ExceptionMessage) {
                changesFlow.emit(Unit)
            }
        }
    }

    override suspend fun editTag(tagTitle: String, newTitle: String, newColor: Int) {
        withContext(ioDispatcher) {
            val message = dataSource.editTag(tagTitle, newTitle, newColor)
            messageFlow.emit(message)
            if (message !is ExceptionMessage) {
                changesFlow.emit(Unit)
            }
        }
    }

    override suspend fun removeTag(tagTitle: String) {
        withContext(ioDispatcher) {
            val message = dataSource.removeTag(tagTitle)
            messageFlow.emit(message)
            if (message !is ExceptionMessage) {
                changesFlow.emit(Unit)
            }
        }
    }

    override suspend fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey) {
        withContext(ioDispatcher) {
            val message = dataSource.removeTagFromLesson(tagTitle, lesson)
            messageFlow.emit(message)
            if (message !is ExceptionMessage) {
                changesFlow.emit(Unit)
            }
        }
    }
}