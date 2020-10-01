package com.mospolytech.mospolyhelper.domain.schedule.usecase

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonLabelRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonLabelKey
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.repository.GroupListRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.SavedIdsRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.TeacherListRepository
import com.mospolytech.mospolyhelper.utils.DefaultSettings
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.StringId
import com.mospolytech.mospolyhelper.utils.StringProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

data class ScheduleLabelDeadline(
    val schedule: Schedule?,
    val labels: Map<LessonLabelKey, Set<String>>,
    val deadlines: Map<String, List<Deadline>>
)

class ScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val groupListRepository: GroupListRepository,
    private val teacherListRepository: TeacherListRepository,
    private val savedIdsRepository: SavedIdsRepository,
    private val lessonLabelRepository: LessonLabelRepository,
    private val deadlineRepository: DeadlinesRepository,
    // TODO: Fix this
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) {
    fun getSchedule(
        group: String,
        isStudent: Boolean,
        refresh: Boolean
    ) = scheduleRepository.getSchedule(group, isStudent, refresh)

    fun getScheduleWithFeatures(
        group: String,
        isStudent: Boolean,
        refresh: Boolean
    ): Flow<ScheduleLabelDeadline> {
        return combine(
            scheduleRepository.getSchedule(group, isStudent, refresh),
            lessonLabelRepository.getAll(),
            flowOf(mapOf<String, List<Deadline>>())
        ) { schedule, labels, deadlines ->
            ScheduleLabelDeadline(schedule, labels, deadlines)
        }
    }

    suspend fun getIdSet(
        messageBlock: (String) -> Unit = { }
    ): Set<Pair<Boolean, String>> {
        val groupList = groupListRepository.getGroupList().map { Pair(true, it)} +
                teacherListRepository.getTeacherList().map { "${it.value} (id${it.key})" }.sorted().map { Pair(false, it)}
        if (groupList.isEmpty()) {
            messageBlock(StringProvider.getString(StringId.GroupListWasntFounded))
        }
        return groupList.toSet()
    }

    fun getSavedIds(): Set<Pair<Boolean, String>> {
        return savedIdsRepository.getSavedIds()
            .toSortedSet(
                comparator
            )
    }

    val comparator = Comparator<Pair<Boolean, String>> { o1, o2 ->
        return@Comparator if (o1.first != o2.first) {
            if (o1.first) -1 else 1
        } else {
            o1.second.compareTo(o2.second)
        }
    }

    fun setSavedIds(savedIds: Set<Pair<Boolean, String>>) {
        savedIdsRepository.setSavedIds(savedIds)
    }

    fun getSelectedSavedId(): String {
        return sharedPreferencesDataSource.getString(
            PreferenceKeys.ScheduleGroupTitle,
            DefaultSettings.ScheduleGroupTitle
        )
    }

    fun setSelectedSavedId(savedId: String) {
        sharedPreferencesDataSource.setString(
            PreferenceKeys.ScheduleGroupTitle,
            savedId
        )
    }

    fun getIsStudent(): Boolean {
        return sharedPreferencesDataSource.getBoolean(
            PreferenceKeys.ScheduleUserTypePreference,
            DefaultSettings.ScheduleUserTypePreference
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
            DefaultSettings.ScheduleShowEmptyLessons
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
            DefaultSettings.ShowEndedLessons
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
            DefaultSettings.ShowCurrentLessons
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
            DefaultSettings.ShowNotStartedLessons
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
            DefaultSettings.FilterTypes
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
            DefaultSettings.ShowImportantLessons
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
            DefaultSettings.ShowAverageLessons
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
            DefaultSettings.ShowNotImportantLessons
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
            DefaultSettings.ShowNotLabeledLessons
        )
    }

    fun setShowNotLabeledLessons(showNotLabeledLessons: Boolean) {
        sharedPreferencesDataSource.setBoolean(
            PreferenceKeys.ShowNotLabeledLessons,
            showNotLabeledLessons
        )
    }
}