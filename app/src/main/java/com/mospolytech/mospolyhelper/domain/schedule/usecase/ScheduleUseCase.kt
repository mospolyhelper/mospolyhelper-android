package com.mospolytech.mospolyhelper.domain.schedule.usecase

import android.util.Log
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.domain.core.repository.PreferencesRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleException
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonDateFilter
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.repository.LessonTagsRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleSourcesRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.coroutines.flow.*

class ScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleSourcesRepository: ScheduleSourcesRepository,
    private val tagRepository: LessonTagsRepository,
    private val deadlineRepository: DeadlinesRepository,
    private val preferences: PreferencesRepository
) {
    val scheduleUpdates = scheduleRepository.dataLastUpdatedObservable

    fun getSchedule(source: ScheduleSource?) = flow {
        if (source != null) {
            emitAll(
                scheduleRepository.getSchedule(source)
                    .onStart { emit(Result0.Loading) }
            )
        } else {
            emit(Result0.Failure(ScheduleException.UserIsNull))
        }
    }


    suspend fun updateSchedule(source: ScheduleSource?) =
        scheduleRepository.updateSchedule(source)

    suspend fun getAnySchedule(onProgressChanged: (Float) -> Unit): SchedulePackList {
        return scheduleRepository.getSchedulePackList(onProgressChanged)
    }

    suspend fun getSchedulePackListLocal(): Result0<SchedulePackList> {
        return scheduleRepository.getSchedulePackListLocal()
    }

    suspend fun getScheduleVersion(source: ScheduleSource) =
        scheduleRepository.getScheduleVersion(source)

    fun getAllUsers(): Flow<List<ScheduleSource>> =
        scheduleSourcesRepository.getScheduleSources()
            .catch { Log.e(TAG, "Flow exception", it) }

    fun getFavoriteScheduleSources() =
        scheduleSourcesRepository.getFavoriteScheduleSources()
        .onEach {
            if (it.isEmpty() && getSelectedScheduleSource().first() != null) {
                setSelectedScheduleSource(null)
            }
        }
        .catch { Log.e(TAG, "Flow exception", it) }

    suspend fun addSavedScheduleUser(source: ScheduleSource) {
        scheduleSourcesRepository.addFavoriteScheduleSource(source)
    }

    suspend fun removeSavedScheduleUser(source: ScheduleSource) {
        scheduleSourcesRepository.removeFavoriteScheduleSource(source)
        if (getSelectedScheduleSource().first() == source) {
            setSelectedScheduleSource(null)
        }
    }

    fun getSelectedScheduleSource() =
        scheduleSourcesRepository.getSelectedScheduleSource()
            .catch { Log.e(TAG, "Flow exception", it) }

    suspend fun setSelectedScheduleSource(source: ScheduleSource?) {
        scheduleSourcesRepository.setSelectedScheduleSource(source)
    }


    fun getShowEmptyLessons() = preferences.dataLastUpdatedFlow.transform {
        if (it == PreferenceKeys.ScheduleShowEmptyLessons) {
            emit(
                preferences.get(
                    PreferenceKeys.ScheduleShowEmptyLessons,
                    PreferenceDefaults.ScheduleShowEmptyLessons
                )
            )
        }
    }.onStart {
        emit(
            preferences.get(
                PreferenceKeys.ScheduleShowEmptyLessons,
                PreferenceDefaults.ScheduleShowEmptyLessons
            )
        )
    }

    fun getLessonDateFilter(): LessonDateFilter {
        return LessonDateFilter(
            preferences.get(
                PreferenceKeys.ShowEndedLessons,
                PreferenceDefaults.ShowEndedLessons
            ),
            true,
            preferences.get(
                PreferenceKeys.ShowNotStartedLessons,
                PreferenceDefaults.ShowNotStartedLessons
            )
        )
    }
    fun setLessonDateFilter(lessonDateFilter: LessonDateFilter) {
        preferences.set(
            PreferenceKeys.ShowEndedLessons,
            lessonDateFilter.showEndedLessons
        )
        preferences.set(
            PreferenceKeys.ShowNotStartedLessons,
            lessonDateFilter.showNotStartedLessons
        )
    }

    fun getAllTags() =
        tagRepository.getAll()

    suspend fun addTag(tag: LessonTag) =
        tagRepository.addTag(tag)

    suspend fun addTagToLesson(tagTitle: String, lesson: LessonTagKey) =
        tagRepository.addTagToLesson(tagTitle, lesson)

    suspend fun editTag(tagTitle: String, newTitle: String, newColor: Int) =
        tagRepository.editTag(tagTitle, newTitle, newColor)

    suspend fun removeTag(tagTitle: String) =
        tagRepository.removeTag(tagTitle)

    suspend fun removeTagFromLesson(tagTitle: String, lesson: LessonTagKey) =
        tagRepository.removeTagFromLesson(tagTitle, lesson)

    fun getAllDeadlines() = flow<Result0<Map<String, List<Deadline>>>> {
        emit(Result0.Success(emptyMap()))
    }
}