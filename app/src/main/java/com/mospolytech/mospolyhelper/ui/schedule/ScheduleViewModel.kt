package com.mospolytech.mospolyhelper.ui.schedule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.repository.schedule.GroupListDao
import com.mospolytech.mospolyhelper.repository.schedule.GroupListRepository
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleDao
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleRepository
import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.ui.schedule.calendar.CalendarViewModel
import com.mospolytech.mospolyhelper.ui.schedule.lesson_info.LessonInfoViewModel
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate


class ScheduleViewModel(
    val schedule: MutableStateFlow<Schedule?>,
    val date: MutableStateFlow<LocalDate>,
    val isSession: MutableStateFlow<Boolean>,
    val groupTitle: MutableStateFlow<String>,
    val scheduleFilter: MutableStateFlow<Schedule.Filter>,
    val showEmptyLessons: MutableStateFlow<Boolean>
) : ViewModelBase(StaticDI.viewModelMediator, ScheduleViewModel::class.java.simpleName) {
    companion object {
        const val ChangeDate = "ChangeDate"
    }
    val scheduleRepository = ScheduleRepository(ScheduleDao())
    val groupListRepository = GroupListRepository(GroupListDao())
    var isAdvancedSearch = false
    var groupList = MutableStateFlow(emptyList<String>())

    val endDownloading: Event0 = Action0()
    var isLoading = true
        private set

    val onMessage: Event1<String> = Action1()

    init {
        subscribe(::handleMessage)
        getGroupList(true)

        combine(this.isSession, this.groupTitle) { isSession, groupTitle ->
            setUpSchedule(isSession, groupTitle, true)
        }.launchIn(viewModelScope)
    }

    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            ChangeDate -> {
                date.value = (message.content as List<*>)[0] as LocalDate
            }
        }
    }

    fun updateSchedule() {
        setUpSchedule(
            this@ScheduleViewModel.isSession.value,
            this@ScheduleViewModel.groupTitle.value,
            true
        )
    }

    private fun setUpSchedule(isSession: Boolean, groupTitle: String, downloadNew: Boolean) {
        isLoading = true
        schedule.value = null
        viewModelScope.async {
            val schedule = if (groupTitle.isEmpty()) {
                null
            } else {
                scheduleRepository.getSchedule(
                    groupTitle,
                    isSession,
                    downloadNew,
                    (onMessage as Action1)::invoke
                )
                    ?: scheduleRepository.getSchedule(
                        groupTitle,
                        isSession,
                        !downloadNew,
                        (onMessage as Action1)::invoke
                    )
            }
            withContext(Dispatchers.Main) {
                this@ScheduleViewModel.schedule.value = schedule
                isLoading = false
                (endDownloading as Action0).invoke()
            }
        }
    }

    suspend fun getAdvancedSearchData(
        groupList: List<String>,
        onProgressChanged: (Float) -> Unit
    ): ScheduleRepository.SchedulePackList {
        return scheduleRepository.getAnySchedules(groupList, onProgressChanged)
    }

    fun goHome() {
        date.value = LocalDate.now()
    }

    fun openCalendar() {
        send(CalendarViewModel::class.java.simpleName, CalendarViewModel.CalendarMode, listOf(
            schedule.value!!,
            scheduleFilter.value!!,
            date.value!!,
            isAdvancedSearch
        ))
    }

    fun openLessonInfo(lesson: Lesson, date: LocalDate) {
        send(LessonInfoViewModel::class.java.simpleName, LessonInfoViewModel.LessonInfo, listOf(
            lesson,
            date
        ))
    }

    fun getGroupList(downloadNew: Boolean) {
        viewModelScope.async {
            val groupList = groupListRepository.getGroupList(downloadNew)
            withContext(Dispatchers.Main) {
                this@ScheduleViewModel.groupList.value = groupList
            }
        }
    }

    class Factory: ViewModelProvider.Factory {
        var schedule: Schedule? = null
        var date: LocalDate? = null
        var isSession: Boolean? = null
        var groupTitle: String? = null
        var scheduleFilter: Schedule.Filter? = null
        var showEmptyLessons: Boolean? = null

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass == ScheduleViewModel::class.java) {
                return ScheduleViewModel(
                    MutableStateFlow(schedule),
                    MutableStateFlow(date ?: LocalDate.now()),
                    MutableStateFlow(isSession ?: false),
                    MutableStateFlow(groupTitle ?: ""),
                    MutableStateFlow(scheduleFilter ?: Schedule.Filter.default),
                    MutableStateFlow(showEmptyLessons ?: false)
                ) as T
            } else {
                throw IllegalArgumentException("${modelClass.name} is not ${ScheduleViewModel::class.java.name}")
            }
        }
    }
}
