package com.mospolytech.mospolyhelper.ui.deadlines.bottomdialog

import android.content.Context
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleDao
import com.mospolytech.mospolyhelper.repository.local.AppDatabase
import com.mospolytech.mospolyhelper.repository.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.deadline.Deadline
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleRepository
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DialogFragmentViewModel(mediator: Mediator<String, ViewModelMessage>,
                              private val deadlinesRepository: DeadlinesRepository) :
    ViewModelBase(mediator, DialogFragmentViewModel::class.java.simpleName) {

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

}