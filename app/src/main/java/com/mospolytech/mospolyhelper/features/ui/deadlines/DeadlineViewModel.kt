package com.mospolytech.mospolyhelper.features.ui.deadlines

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage


class DeadlineViewModel(mediator: Mediator<String, ViewModelMessage>,
                        private val deadlinesRepository: DeadlinesRepository) :
    ViewModelBase(mediator, DeadlineViewModel::class.java.simpleName) {

    val edit : MutableLiveData<Deadline> = MutableLiveData()
    val delete : MutableLiveData<Deadline> = MutableLiveData()
    val nameReceiver : MutableLiveData<String> = MutableLiveData()
    private var findstr: String = ""


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

}