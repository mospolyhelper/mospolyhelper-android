package com.mospolytech.mospolyhelper.features.ui.schedule

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.GroupListRepositoryImpl
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonLabelRepository
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleLabelDeadline
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.calendar.CalendarViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.LessonInfoViewModel
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.KoinComponent
import java.time.LocalDate


class ScheduleViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val scheduleUseCase: ScheduleUseCase,
    val schedule: MutableStateFlow<ScheduleLabelDeadline>,
    val date: MutableStateFlow<LocalDate>,
    val isSession: MutableStateFlow<Boolean>,
    val groupTitle: MutableStateFlow<String>,
    val scheduleFilter: MutableStateFlow<Schedule.Filter>,
    val showEmptyLessons: MutableStateFlow<Boolean>
) : ViewModelBase(mediator, ScheduleViewModel::class.java.simpleName), KoinComponent {
    companion object {
        const val MessageChangeDate = "ChangeDate"
        const val MessageSetAdvancedSearchSchedule = "SetAdvancedSearchSchedule"
    }
    var isAdvancedSearch = false
    var groupList = MutableStateFlow(emptyList<String>())

    var isLoading = MutableStateFlow(true)
    private var firstLoading = true

    val onMessage: Event1<String> = Action1()

    init {
        subscribe(::handleMessage)
        getGroupList(true)

        combine(this.isSession, this.groupTitle) { isSession, groupTitle ->
            setUpSchedule(isSession, groupTitle, !firstLoading)
            firstLoading = false
        }.launchIn(viewModelScope)
    }


    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            MessageChangeDate -> {
                date.value = message.content[0] as LocalDate
            }
            MessageSetAdvancedSearchSchedule -> {
                isLoading.value = true
                isAdvancedSearch = true
                schedule.value = ScheduleLabelDeadline(message.content[0] as Schedule, emptyMap(), emptyMap())
                isLoading.value = false
            }
        }
    }

    fun updateSchedule() {
        setUpSchedule(
            isSession.value,
            groupTitle.value,
            true
        )
    }

    private fun setUpSchedule(isSession: Boolean, groupTitle: String, downloadNew: Boolean) {
        isLoading.value = true
        viewModelScope.async {
            scheduleUseCase.getScheduleWithFeatures(
                groupTitle,
                isSession,
                downloadNew
            ).collect {
                withContext(Dispatchers.Main) {
                    this@ScheduleViewModel.schedule.value = it
                    isLoading.value = false
                }
            }
        }
    }

    fun goHome() {
        date.value = LocalDate.now()
    }

    fun openCalendar() {
        send(
            CalendarViewModel::class.java.simpleName,
            CalendarViewModel.CalendarMode,
            schedule.value!!,
            scheduleFilter.value!!,
            date.value!!,
            isAdvancedSearch
        )
    }

    fun openLessonInfo(lesson: Lesson, date: LocalDate) {
        send(
            LessonInfoViewModel::class.java.simpleName,
            LessonInfoViewModel.LessonInfo,
            lesson,
            date
        )
    }

    private fun getGroupList(refresh: Boolean) {
        viewModelScope.async {
            val groupList = scheduleUseCase.getGroupList(refresh)
            withContext(Dispatchers.Main) {
                this@ScheduleViewModel.groupList.value = groupList
            }
        }
    }

    class Factory {
        companion object {
            fun create(
                mediator: Mediator<String, ViewModelMessage>,
                scheduleUseCase: ScheduleUseCase,
                preferences: SharedPreferences
            ): ScheduleViewModel {
                val dateFilter = Schedule.Filter.DateFilter.values()[
                        preferences.getInt(
                            PreferenceKeys.ScheduleDateFilter,
                            Schedule.Filter.default.dateFilter.ordinal
                        )
                ]
                val sessionFilter = preferences.getBoolean(
                    PreferenceKeys.ScheduleSessionFilter,
                    Schedule.Filter.default.sessionFilter
                )

                val scheduleFilter = Schedule.Filter.Builder(Schedule.Filter.default)
                    .dateFilter(dateFilter)
                    .sessionFilter(sessionFilter)
                    .build()

                val groupTitle = preferences.getString(
                    PreferenceKeys.ScheduleGroupTitle,
                    DefaultSettings.ScheduleGroupTitle
                )

                val isSession = try {
                    preferences.getBoolean(
                        PreferenceKeys.ScheduleTypePreference,
                        DefaultSettings.ScheduleTypePreference
                    )
                } catch (e: Exception) {
                    preferences.getInt(PreferenceKeys.ScheduleTypePreference, 0) == 1;
                }

                val showEmptyLessons = preferences.getBoolean(
                    PreferenceKeys.ScheduleShowEmptyLessons,
                    DefaultSettings.ScheduleShowEmptyLessons
                )
                return ScheduleViewModel(
                    mediator,
                    scheduleUseCase,
                    MutableStateFlow(ScheduleLabelDeadline(null, emptyMap(), emptyMap())),
                    MutableStateFlow(LocalDate.now()),
                    MutableStateFlow(isSession),
                    MutableStateFlow(groupTitle ?: ""),
                    MutableStateFlow(scheduleFilter),
                    MutableStateFlow(showEmptyLessons)
                )
            }
        }
    }
}
