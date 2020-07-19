package com.mospolytech.mospolyhelper.ui.deadlines

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.App.Companion.context
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext


class DeadlineViewModel(mediator: Mediator<String, ViewModelMessage>,
                        private val deadlinesRepository: DeadlinesRepository,
                        private val scheduleRepository: ScheduleRepository) :
    ViewModelBase(mediator, DeadlineViewModel::class.java.simpleName) {
    //AndroidViewModel(app) {
    companion object {
        const val DeadlineInfo = "DeadlinesInfo"
    }

    val edit : MutableLiveData<Deadline> = MutableLiveData()
    val delete : MutableLiveData<Deadline> = MutableLiveData()
    val nameReceiver : MutableLiveData<String> = MutableLiveData()
    private var findstr: String = ""
    val schedule: MutableStateFlow<Schedule?> = MutableStateFlow(null)
    val onMessage: Event1<String> = Action1()


    val data = deadlinesRepository.getDeadlines()
    val dataCurrent = deadlinesRepository.getDeadlinesCurrent()
    val foundData =  deadlinesRepository.foundData

    fun setName(name: String) {
        nameReceiver.value = name
    }

    fun saveInformation(deadline: Deadline) {
        deadlinesRepository.insertDeadline(deadline)
        if (foundData.hasActiveObservers()) {
            deadlinesRepository.findItem(findstr)
        }
    }

    fun deleteOne(deadline: Deadline) {
        deadlinesRepository.deleteDeadline(deadline)
        if (foundData.hasActiveObservers()) {
            deadlinesRepository.findItem(findstr)
        }
    }

    fun setCompleted(deadline: Deadline) {
        deadline.completed = !deadline.completed
        deadlinesRepository.updateDeadline(deadline)
        if (foundData.hasActiveObservers()) {
            deadlinesRepository.findItem(findstr)
        }
    }

    fun setPinned(deadline: Deadline) {
        deadline.pinned = !deadline.pinned
        deadlinesRepository.updateDeadline(deadline)
        if (foundData.hasActiveObservers()) {
            deadlinesRepository.findItem(findstr)
        }
    }

    fun edit(d: Deadline) {
        edit.value = d
        if (foundData.hasActiveObservers()) {
            deadlinesRepository.findItem(findstr)
        }
    }

    fun delete(d: Deadline) {
        delete.value = d
        if (foundData.hasActiveObservers()) {
            deadlinesRepository.findItem(findstr)
        }
    }

    override fun onCleared() {
        super.onCleared()
        deadlinesRepository.cancel()
    }

    fun find(name: String) {
        findstr = name
        deadlinesRepository.findItem(name)
    }

    fun clearObserveData(a: LifecycleOwner) {
        data.removeObservers(a)
    }

    fun clearObserveDataCur(a: LifecycleOwner) {
        dataCurrent.removeObservers(a)
    }

    fun clearObserveFind(a: LifecycleOwner) {
        foundData.removeObservers(a)
    }

    fun getLessons(): Set<String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val groupTitle = prefs.getString(
            PreferenceKeys.ScheduleGroupTitle,
            DefaultSettings.ScheduleGroupTitle)
        setUpSchedule(false, groupTitle!!, false)
        return scheduleRepository.allDataFromSchedule(schedule.value!!).lessonTitles
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
                this@DeadlineViewModel.schedule.value = schedule
            }
        }
    }
}