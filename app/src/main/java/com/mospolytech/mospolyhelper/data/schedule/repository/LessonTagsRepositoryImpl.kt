package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.LessonTagsLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.repository.LessonTagsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LessonTagsRepositoryImpl(
    private val dataSource: LessonTagsLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) : LessonTagsRepository {

    private val changesFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 64)

    override fun getAll() = flow {
        emit(dataSource.getAll())
        emitAll(changesFlow.map { dataSource.getAll() })
    }

    override suspend fun addTag(tag: LessonTag) {
        withContext(ioDispatcher) {
            dataSource.addTag(tag)
            changesFlow.emit(Unit)
        }
    }

    override suspend fun editTag(tagTitle: String, newTitle: String, newColor: Int) {
        withContext(ioDispatcher) {
            dataSource.editTag(tagTitle, newTitle, newColor)
            changesFlow.emit(Unit)
        }
    }

    override suspend fun removeTag(tagTitle: String) {
        withContext(ioDispatcher) {
            dataSource.removeTag(tagTitle)
            changesFlow.emit(Unit)
        }
    }

    override suspend fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey) {
        withContext(ioDispatcher) {
            dataSource.removeTagFromLesson(tagTitle, lesson)
            changesFlow.emit(Unit)
        }
    }
}