package com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleVersionDb
import com.mospolytech.mospolyhelper.domain.schedule.model.AdvancedSearchSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleFilters
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.utils.onFailure
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdvancedSearchViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: ScheduleUseCase
) : ViewModelBase(
        mediator,
        AdvancedSearchViewModel::class.java.simpleName
) {
    val checkedGroups = ObservableArrayList<Int>()
    val checkedLessonTypes = ObservableArrayList<Int>()
    val checkedTeachers = ObservableArrayList<Int>()
    val checkedLessonTitles = ObservableArrayList<Int>()
    val checkedAuditoriums = ObservableArrayList<Int>()


    private val user = AdvancedSearchSchedule(
        ScheduleFilters(emptySet(), emptySet(), emptySet(), emptySet(), emptySet())
    )

    private val _scheduleVersion = MutableStateFlow<ScheduleVersionDb?>(null)
    val scheduleVersion: StateFlow<ScheduleVersionDb?> = _scheduleVersion

    private val _schedulePackList = MutableStateFlow<SchedulePackList?>(null)
    val schedulePackList: StateFlow<SchedulePackList?> = _schedulePackList

    var lessonTitles = schedulePackList.map { it?.lessonTitles ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    var lessonTypes = schedulePackList.map { it?.lessonTypes ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    var lessonTeachers = schedulePackList.map { it?.lessonTeachers ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    var lessonGroups = schedulePackList.map { it?.lessonGroups ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    var lessonAuditoriums = schedulePackList.map { it?.lessonAuditoriums ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    init {
       viewModelScope.launch {
           _scheduleVersion.value = useCase.getScheduleVersion(user)
       }
    }

    suspend fun getAdvancedSearchData(onProgressChanged: (Float) -> Unit) {
        _schedulePackList.value = useCase.getAnySchedule(onProgressChanged)
        _scheduleVersion.value = useCase.getScheduleVersion(user)
    }

    suspend fun getAdvancedSearchDataLocal() {
        useCase.getSchedulePackListLocal().onSuccess {
            _schedulePackList.value = it
        }.onFailure {
            _scheduleVersion.value = null
        }
    }

    fun getScheduleFilters() =
        ScheduleFilters(
            titles = checkedLessonTitles.map { lessonTitles.value[it] }.toSet(),
            types = checkedLessonTypes.map { lessonTypes.value[it] }.toSet(),
            teachers = checkedTeachers.map { lessonTeachers.value[it] }.toSet(),
            groups = checkedGroups.map { lessonGroups.value[it] }.toSet(),
            auditoriums = checkedAuditoriums.map { lessonAuditoriums.value[it] }.toSet()
        )
}