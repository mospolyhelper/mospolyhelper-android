package com.mospolytech.mospolyhelper.ui.deadlines.bottomdialog

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleDao
import com.mospolytech.mospolyhelper.repository.local.AppDatabase
import com.mospolytech.mospolyhelper.repository.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.deadline.Deadline
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleRepository
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.DefaultSettings
import com.mospolytech.mospolyhelper.utils.Event1
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow


class DialogFragmentViewModel(mediator: Mediator<String, ViewModelMessage>,
                              private val deadlinesRepository: DeadlinesRepository,
                              private val scheduleRepository: ScheduleRepository) :
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
        val groupTitle = prefs.getString(
            PreferenceKeys.ScheduleGroupTitle,
            DefaultSettings.ScheduleGroupTitle)
        val isSession =  prefs.getBoolean(
            PreferenceKeys.ScheduleTypePreference,
            DefaultSettings.ScheduleTypePreference
        )
        setUpSchedule(isSession, groupTitle!!, false)
        return this@DialogFragmentViewModel.schedule.value?.let {
            scheduleRepository.allDataFromSchedule(
                it
            ).lessonTitles
        }
    }

    private fun setUpSchedule(isSession: Boolean, groupTitle: String, downloadNew: Boolean) {
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
                this@DialogFragmentViewModel.schedule.value = schedule
            }
        }
    }

}