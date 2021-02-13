package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.TagLocalDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.Tag
import com.mospolytech.mospolyhelper.utils.Change
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class TagRepository(
    private val dataSource: TagLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
    private val changesFlow = MutableSharedFlow<Change<List<Tag>>>(extraBufferCapacity = 64)

    suspend fun remove(lesson: Lesson, tag: Tag) =
        withContext(ioDispatcher) {
            val tags = dataSource.getAll()
            var oldList = emptyList<Tag>()
            var changedList = emptyList<Tag>()
            val newTags = tags.mapValues {
                if (it.key == LessonTagKey.fromLesson(lesson)) {
                    oldList = it.value
                    changedList = it.value - tag
                    changedList
                } else {
                    it.value
                }
            }
            dataSource.setAll(newTags)
            changesFlow.emit(Change.Edited(oldList, changedList))
        }

    suspend fun add(lesson: Lesson, tag: Tag) =
        withContext(ioDispatcher) {
            val tags = dataSource.getAll()
            var oldList = emptyList<Tag>()
            var changedList = emptyList<Tag>()
            val newTags = tags.mapValues {
                if (it.key == LessonTagKey.fromLesson(lesson)) {
                    oldList = it.value
                    changedList = it.value + tag
                    changedList
                } else {
                    it.value
                }
            }
            dataSource.setAll(newTags)
            changesFlow.emit(Change.Edited(oldList, changedList))
        }

    fun getAll() = flow {
        val initial = dataSource.getAll()

        changesFlow
            .scan(initial) { tags, change ->
                when (change) {
                    is Change.Edited -> tags.mapValues { entry ->
                        if (entry.value == change.old) {
                            change.new
                        } else {
                            entry.value
                        }
                    }
                    else -> dataSource.getAll()
                }
            }.let { emitAll(it) }
    }
}