package com.mospolytech.mospolyhelper.domain.schedule.usecase

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.TagRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.Tag
import com.mospolytech.mospolyhelper.domain.schedule.repository.GroupListRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.SavedIdsRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.TeacherListRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.StringId
import com.mospolytech.mospolyhelper.utils.StringProvider
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class ScheduleTagsDeadline(
    val schedule: Schedule?,
    val tags: Map<LessonTagKey, List<Tag>>,
    val deadlines: Map<String, List<Deadline>>
)

class ScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val groupListRepository: GroupListRepository,
    private val teacherListRepository: TeacherListRepository,
    private val savedIdsRepository: SavedIdsRepository,
    private val tagRepository: TagRepository,
    private val deadlineRepository: DeadlinesRepository,
    // TODO: Fix this (Replace By Rep)
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
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
            ScheduleTagsDeadline(schedule, tags, deadlines)
        }
    }

    suspend fun addTag(lesson: Lesson, tag: Tag) {
        tagRepository.add(lesson, tag)
    }

    suspend fun removeTag(lesson: Lesson, tag: Tag) {
        tagRepository.remove(lesson, tag)
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
        return savedIdsRepository.getSavedIds()
            .toSortedSet(
                comparator
            )
    }

    private val comparator = Comparator<UserSchedule> { o1, o2 ->
        return@Comparator if (o1.title != o2.title) {
            o1.title.compareTo(o2.title)
        } else {
            o1.id.compareTo(o2.id)
        }
    }

    fun setSavedIds(savedIds: Set<UserSchedule>) {
        savedIdsRepository.setSavedIds(savedIds)
    }

    fun getSelectedSavedId(): UserSchedule? {
        return try {
            val j = sharedPreferencesDataSource.getString(
                PreferenceKeys.ScheduleUser,
                PreferenceDefaults.ScheduleUser
            )
            Json.decodeFromString<UserSchedule>(j)
        } catch (e: Exception) {
            null
        }
    }

    fun setSelectedSavedId(savedId: UserSchedule?) {
        sharedPreferencesDataSource.setString(
            PreferenceKeys.ScheduleUser,
            if (savedId == null) "" else Json.encodeToString(savedId)
        )
    }

    fun setIsStudent(isStudent: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ScheduleUserTypePreference,
            isStudent
        )
    }

    fun getShowEmptyLessons(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ScheduleShowEmptyLessons,
            PreferenceDefaults.ScheduleShowEmptyLessons
        )
    }

    fun setShowEmptyLessons(showEmptyLessons: Boolean) {
        return sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ScheduleShowEmptyLessons,
            showEmptyLessons
        )
    }

    fun getShowEndedLessons(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ShowEndedLessons,
            PreferenceDefaults.ShowEndedLessons
        )
    }

    fun setShowEndedLessons(showEndedLessons: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ShowEndedLessons,
            showEndedLessons
        )
    }

    fun getShowCurrentLessons(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ShowCurrentLessons,
            PreferenceDefaults.ShowCurrentLessons
        )
    }

    fun setShowCurrentLessons(showCurrentLessons: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ShowCurrentLessons,
            showCurrentLessons
        )
    }

    fun getShowNotStartedLessons(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ShowNotStartedLessons,
            PreferenceDefaults.ShowNotStartedLessons
        )
    }

    fun setShowNotStartedLessons(showNotStartedLessons: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ShowNotStartedLessons,
            showNotStartedLessons
        )
    }

    fun getFilterTypes(): Set<String> {
        return sharedPreferencesDataSource.getStringSet(
            PreferenceKeys.FilterTypes,
            PreferenceDefaults.FilterTypes
        )
    }

    fun setFilterTypes(filterTypes: Set<String>) {
        sharedPreferencesDataSource.setStringSet(
            PreferenceKeys.FilterTypes,
            filterTypes
        )
    }

    fun getShowImportantLessons(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ShowImportantLessons,
            PreferenceDefaults.ShowImportantLessons
        )
    }

    fun setShowImportantLessons(showImportantLessons: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ShowImportantLessons,
            showImportantLessons
        )
    }

    fun getShowAverageLessons(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ShowAverageLessons,
            PreferenceDefaults.ShowAverageLessons
        )
    }

    fun setShowAverageLessons(showAverageLessons: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ShowAverageLessons,
            showAverageLessons
        )
    }

    fun getShowNotImportantLessons(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ShowNotImportantLessons,
            PreferenceDefaults.ShowNotImportantLessons
        )
    }

    fun setShowNotImportantLessons(showNotImportantLessons: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ShowNotImportantLessons,
            showNotImportantLessons
        )
    }

    fun getShowNotLabeledLessons(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ShowNotLabeledLessons,
            PreferenceDefaults.ShowNotLabeledLessons
        )
    }

    fun setShowNotLabeledLessons(showNotLabeledLessons: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ShowNotLabeledLessons,
            showNotLabeledLessons
        )
    }
}