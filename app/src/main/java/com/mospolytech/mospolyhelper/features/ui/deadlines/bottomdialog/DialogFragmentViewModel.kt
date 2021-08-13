package com.mospolytech.mospolyhelper.features.ui.deadlines.bottomdialog

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.domain.core.repository.PreferencesRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Event1
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class DialogFragmentViewModel(mediator: Mediator<String, ViewModelMessage>,
                              private val deadlinesRepository: DeadlinesRepository,
                              private val scheduleRepository: ScheduleRepository,
                              private val prefs: PreferencesRepository
) :
    ViewModelBase(mediator, DialogFragmentViewModel::class.java.simpleName) {

    val schedule: MutableStateFlow<Schedule?> = MutableStateFlow(null)
    val onMessage: Event1<String> = Action1()

    fun newRepository() {
        deadlinesRepository.newJob()
    }

    fun saveInformation(deadline: Deadline) {
        deadlinesRepository.insertDeadline(deadline)

    }

    fun updateOne(deadline: Deadline) {
        deadlinesRepository.updateDeadline(deadline)

    }




    override fun onCleared() {
        super.onCleared()
        deadlinesRepository.cancel()
    }

    fun getLessons(): Set<String>? {
        val user = try {
            Json.decodeFromString<ScheduleSource>(prefs.get(
                PreferenceKeys.ScheduleUser,
                PreferenceDefaults.ScheduleUser))
        } catch (e: Exception) {
            null
        }
        setUpSchedule(user)
        return this@DialogFragmentViewModel.schedule.value?.let {
            it.dailySchedules.flatMap { it.flatMap { it.lessons.map { it.title } } }.toSortedSet()
        }
    }

    private fun setUpSchedule(source: ScheduleSource?, downloadNew: Boolean = false) {
        viewModelScope.async {
            if (source == null) {
                withContext(Dispatchers.Main) {
                    this@DialogFragmentViewModel.schedule.value = null
                }
            } else {
                // TODO: Fix isStudentConstant
                scheduleRepository.getSchedule(
                    source
                ).collect {
                    withContext(Dispatchers.Main) {
                        this@DialogFragmentViewModel.schedule.value = it.getOrNull()
                    }
                }
            }
        }
    }

}