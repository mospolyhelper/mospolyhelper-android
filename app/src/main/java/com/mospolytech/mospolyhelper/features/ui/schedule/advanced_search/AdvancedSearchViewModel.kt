package com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.data.schedule.repository.GroupListRepositoryImpl
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import com.mospolytech.mospolyhelper.domain.schedule.repository.GroupListRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.utils.filter
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdvancedSearchViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val scheduleRepository: ScheduleRepository,
    private val groupListRepository: GroupListRepository
) :
    ViewModelBase(
        mediator,
        AdvancedSearchViewModel::class.java.simpleName
) {
    var checkedGroups = ObservableArrayList<Int>()
    var checkedLessonTypes = ObservableArrayList<Int>()
    var checkedTeachers = ObservableArrayList<Int>()
    var checkedLessonTitles = ObservableArrayList<Int>()
    var checkedAuditoriums = ObservableArrayList<Int>()
    var schedules: Iterable<Schedule?> = emptyList()
    var lessonTitles = emptyList<String>()
    var lessonTeachers = emptyList<String>()
    var lessonAuditoriums = emptyList<String>()
    var lessonTypes = emptyList<String>()
    var groupList = emptyList<String>()

    init {
     viewModelScope.launch {
         groupList = groupListRepository.getGroupList()
     }
    }

    suspend fun getAdvancedSearchData(
        groupList: List<String>,
        onProgressChanged: (Float) -> Unit
    ): SchedulePackList {
        return scheduleRepository.getAnySchedules(groupList, true, onProgressChanged)
    }

    suspend fun sendSchedule() {
        withContext(Dispatchers.Main) {
            send(
                ScheduleViewModel::class.java.simpleName,
                ScheduleViewModel.MessageSetAdvancedSearchSchedule,
                null
            )
        }
        val newSchedule = schedules.filter(
            titles = if (checkedLessonTitles.isEmpty()) lessonTitles.toSet()
            else checkedLessonTitles.map { lessonTitles[it] }.toSet(),
            types = if (checkedLessonTypes.isEmpty()) lessonTypes.toSet()
            else checkedLessonTypes.map { lessonTypes[it] }.toSet(),
            auditoriums = if (checkedAuditoriums.isEmpty()) lessonAuditoriums.toSet()
            else checkedAuditoriums.map { lessonAuditoriums[it] }.toSet(),
            teachers = if (checkedTeachers.isEmpty()) lessonTeachers.toSet()
            else checkedTeachers.map { lessonTeachers[it] }.toSet()
        )
        withContext(Dispatchers.Main) {
            send(
                ScheduleViewModel::class.java.simpleName,
                ScheduleViewModel.MessageSetAdvancedSearchSchedule,
                newSchedule
            )
        }
    }
}