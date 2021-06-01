package com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search

import androidx.databinding.ObservableArrayList
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleFilters
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdvancedSearchViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val scheduleRepository: ScheduleRepository
) :
    ViewModelBase(
        mediator,
        AdvancedSearchViewModel::class.java.simpleName
) {
    val checkedGroups = ObservableArrayList<Int>()
    val checkedLessonTypes = ObservableArrayList<Int>()
    val checkedTeachers = ObservableArrayList<Int>()
    val checkedLessonTitles = ObservableArrayList<Int>()
    val checkedAuditoriums = ObservableArrayList<Int>()
    var lessonTitles = emptyList<String>()
    var lessonTeachers = emptyList<String>()
    var lessonAuditoriums = emptyList<String>()
    var lessonTypes = emptyList<String>()
    var lessonGroups = emptyList<String>()

    suspend fun getAdvancedSearchData(
        onProgressChanged: (Float) -> Unit
    ): SchedulePackList {
        try {
            return scheduleRepository.getAnySchedules(onProgressChanged)
        } catch (e: Throwable) {
            throw e
        }
    }

    suspend fun sendSchedule() {
        withContext(Dispatchers.Main) {
            send(
                ScheduleViewModel::class.java.simpleName,
                ScheduleViewModel.MessageSetAdvancedSearchSchedule,
                ScheduleFilters(
                    titles = if (checkedLessonTitles.isEmpty()) lessonTitles.toSet()
                    else checkedLessonTitles.map { lessonTitles[it] }.toSet(),
                    types = if (checkedLessonTypes.isEmpty()) lessonTypes.toSet()
                    else checkedLessonTypes.map { lessonTypes[it] }.toSet(),
                    teachers = if (checkedTeachers.isEmpty()) lessonTeachers.toSet()
                    else checkedTeachers.map { lessonTeachers[it] }.toSet(),
                    groups = if (checkedGroups.isEmpty()) lessonGroups.toSet()
                    else checkedGroups.map { lessonGroups[it] }.toSet(),
                    auditoriums = if (checkedAuditoriums.isEmpty()) lessonAuditoriums.toSet()
                    else checkedAuditoriums.map { lessonAuditoriums[it] }.toSet()
                )
            )
        }
    }
}