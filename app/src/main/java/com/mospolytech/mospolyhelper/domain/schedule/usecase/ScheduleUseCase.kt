package com.mospolytech.mospolyhelper.domain.schedule.usecase

import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.setAsJson
import com.mospolytech.mospolyhelper.domain.core.repository.PreferencesRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.repository.*
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

data class ScheduleTagsDeadline(
    val schedule: Schedule?,
    val tags: List<LessonTag>,
    val deadlines: Map<String, List<Deadline>>
)

class ScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val groupListRepository: GroupListRepository,
    private val teacherListRepository: TeacherListRepository,
    private val savedIdsRepository: SavedIdsRepository,
    private val tagRepository: LessonTagsRepository,
    private val deadlineRepository: DeadlinesRepository,
    val preferences: PreferencesRepository
) {
    fun getSchedule(
        user: UserSchedule?,
        refresh: Boolean
    ) = scheduleRepository.getSchedule(user, refresh)

    fun getScheduleWithFeatures(
        user: UserSchedule?,
        refresh: Boolean
    ): Flow<ScheduleTagsDeadline> {
        return combine(
            if (user == null) flowOf(null) else scheduleRepository.getSchedule(user, refresh),
            tagRepository.getAll(),
            flowOf(mapOf<String, List<Deadline>>())
        ) { schedule, tags, deadlines ->
            ScheduleTagsDeadline(schedule, (tags as Result2.Success).value, deadlines)
        }
    }

    suspend fun getIdSet(
        messageBlock: (String) -> Unit = { }
    ): Set<UserSchedule> {
        val groupList = groupListRepository.getGroupList().map { StudentSchedule(it, it) } +
                teacherListRepository.getTeacherList().map { TeacherSchedule(it.key, it.value) }.sortedBy { it.title + it.id }
        if (groupList.isEmpty()) {
            messageBlock(StringProvider.getString(StringId.GroupListWasntFounded))
        }
        return groupList.toSet()
    }

    fun getSavedIds(): Set<UserSchedule> {
        return savedIdsRepository.getSavedIds().toSortedSet()
    }

    fun setSavedIds(savedIds: Set<UserSchedule>) {
        savedIdsRepository.setSavedIds(savedIds)
    }

    fun getSelectedSavedId(): UserSchedule? {
        return preferences.getFromJson(PreferenceKeys.ScheduleUser)
    }

    fun setSelectedSavedId(savedId: UserSchedule?) {
        preferences.setAsJson(PreferenceKeys.ScheduleUser, savedId)
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

    fun getShowEndedLessons(): Boolean {
        return preferences.get(
            PreferenceKeys.ShowEndedLessons,
            PreferenceDefaults.ShowEndedLessons
        )
    }

    fun setShowEndedLessons(showEndedLessons: Boolean) {
        preferences.set(
            PreferenceKeys.ShowEndedLessons,
            showEndedLessons
        )
    }

    fun getShowCurrentLessons(): Boolean {
        return preferences.get(
            PreferenceKeys.ShowCurrentLessons,
            PreferenceDefaults.ShowCurrentLessons
        )
    }

    fun setShowCurrentLessons(showCurrentLessons: Boolean) {
        preferences.set(
            PreferenceKeys.ShowCurrentLessons,
            showCurrentLessons
        )
    }

    fun getShowNotStartedLessons(): Boolean {
        return preferences.get(
            PreferenceKeys.ShowNotStartedLessons,
            PreferenceDefaults.ShowNotStartedLessons
        )
    }

    fun setShowNotStartedLessons(showNotStartedLessons: Boolean) {
        preferences.set(
            PreferenceKeys.ShowNotStartedLessons,
            showNotStartedLessons
        )
    }

    fun getFilterTypes(): Set<String> {
        return preferences.get(
            PreferenceKeys.FilterTypes,
            PreferenceDefaults.FilterTypes
        )
    }

    fun setFilterTypes(filterTypes: Set<String>) {
        preferences.set(
            PreferenceKeys.FilterTypes,
            filterTypes
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
}