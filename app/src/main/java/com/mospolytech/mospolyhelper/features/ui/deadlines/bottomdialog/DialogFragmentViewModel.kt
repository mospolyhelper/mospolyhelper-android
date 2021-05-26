package com.mospolytech.mospolyhelper.features.ui.deadlines.bottomdialog

import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
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
                              private val scheduleRepository: ScheduleRepository
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
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.context)
        val user = try {
            Json.decodeFromString<UserSchedule>(prefs.getString(
                PreferenceKeys.ScheduleUser,
                PreferenceDefaults.ScheduleUser)!!)
        } catch (e: Exception) {
            null
        }
        setUpSchedule(user)
        return this@DialogFragmentViewModel.schedule.value?.let {
            ScheduleRepositoryImpl.allDataFromSchedule(
                it
            ).lessonTitles
        }
    }

    private fun setUpSchedule(user: UserSchedule?, downloadNew: Boolean = false) {
        viewModelScope.async {
            if (user == null) {
                withContext(Dispatchers.Main) {
                    this@DialogFragmentViewModel.schedule.value = null
                }
            } else {
                // TODO: Fix isStudentConstant
                scheduleRepository.getSchedule(
                    user
                ).collect {
                    withContext(Dispatchers.Main) {
                        this@DialogFragmentViewModel.schedule.value = it
                    }
                }
            }
        }
    }

}