package com.mospolytech.mospolyhelper.domain.schedule.usecase

import android.util.Log
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.domain.core.repository.PreferencesRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonDateFilter
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.repository.*
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.flow.*

class ScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleUsersRepository: ScheduleUsersRepository,
    private val tagRepository: LessonTagsRepository,
    private val deadlineRepository: DeadlinesRepository,
    private val preferences: PreferencesRepository
) {
    val scheduleUpdates = scheduleRepository.dataLastUpdatedObservable

    fun getSchedule(user: UserSchedule?) =
        scheduleRepository.getSchedule(user)
            .onStart { emit(Result0.Loading) }

    suspend fun updateSchedule(user: UserSchedule?) =
        scheduleRepository.updateSchedule(user)

    suspend fun getAnySchedule(onProgressChanged: (Float) -> Unit): SchedulePackList {
        return scheduleRepository.getSchedulePackList(onProgressChanged)
    }

    suspend fun getSchedulePackListLocal(): Result0<SchedulePackList> {
        return scheduleRepository.getSchedulePackListLocal()
    }

    suspend fun getScheduleVersion(user: UserSchedule) =
        scheduleRepository.getScheduleVersion(user)

    fun getAllUsers(): Flow<List<UserSchedule>> =
        scheduleUsersRepository.getScheduleUsers()
            .catch { Log.e(TAG, "Flow exception", it) }

    fun getSavedUsers() = scheduleUsersRepository.getSavedUsers()
        .onEach {
            if (it.isEmpty() && getCurrentUser().first() != null) {
                setCurrentUser(null)
            }
        }
        .catch { Log.e(TAG, "Flow exception", it) }

    suspend fun setSavedUsers(users: List<UserSchedule>) {
        scheduleUsersRepository.setSavedUsers(users)
    }

    suspend fun addSavedScheduleUser(user: UserSchedule) {
        scheduleUsersRepository.addSavedUser(user)
    }

    suspend fun removeSavedScheduleUser(user: UserSchedule) {
        scheduleUsersRepository.removeSavedUser(user)
        if (getCurrentUser().first() == user) {
            setCurrentUser(null)
        }
    }

    fun getCurrentUser() =
        scheduleUsersRepository.getCurrentUser()
            .catch { Log.e(TAG, "Flow exception", it) }

    suspend fun setCurrentUser(user: UserSchedule?) {
        scheduleUsersRepository.setCurrentUser(user)
    }


    fun getShowEmptyLessons(): Boolean {
        return preferences.get(
            PreferenceKeys.ScheduleShowEmptyLessons,
            PreferenceDefaults.ScheduleShowEmptyLessons
        )
    }
    fun setShowEmptyLessons(showEmptyLessons: Boolean) {
        return preferences.set(
            PreferenceKeys.ScheduleShowEmptyLessons,
            showEmptyLessons
        )
    }

    fun getLessonDateFilter(): LessonDateFilter {
        return LessonDateFilter(
            preferences.get(
                PreferenceKeys.ShowEndedLessons,
                PreferenceDefaults.ShowEndedLessons
            ),
            preferences.get(
                PreferenceKeys.ShowCurrentLessons,
                PreferenceDefaults.ShowCurrentLessons
            ),
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
            PreferenceKeys.ShowCurrentLessons,
            lessonDateFilter.showCurrentLessons
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