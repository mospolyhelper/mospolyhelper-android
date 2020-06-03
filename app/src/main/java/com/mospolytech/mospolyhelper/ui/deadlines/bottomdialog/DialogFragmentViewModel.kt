package com.mospolytech.mospolyhelper.ui.deadlines.bottomdialog

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.mospolytech.mospolyhelper.repository.database.AppDatabase
import com.mospolytech.mospolyhelper.repository.database.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.database.entity.Deadline
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.deadlines.DeadlineViewModel
import com.mospolytech.mospolyhelper.utils.ContextProvider


class DialogFragmentViewModel/*(app: Application)*/ :
    /*AndroidViewModel(app) {*/
    ViewModelBase(Mediator(), DialogFragmentViewModel::class.java.simpleName) {

    private var database: AppDatabase = AppDatabase.getDatabase(ContextProvider.context as Context)
    private val deadlinesRepository = DeadlinesRepository(database)

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