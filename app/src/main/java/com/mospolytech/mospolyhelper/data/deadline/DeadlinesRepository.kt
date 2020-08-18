package com.mospolytech.mospolyhelper.data.deadline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mospolytech.mospolyhelper.data.core.AppDatabase
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DeadlinesRepository(appDatabase: AppDatabase): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val foundData: MutableLiveData<List<Deadline>> = MutableLiveData()

    private var job : Job = Job()

    private var deadlineDAO = appDatabase.getDeadlinesDAO()

    fun newJob() {
        job = Job()
    }

    fun getDeadlines(): LiveData<List<Deadline>> {
        return deadlineDAO.getAllLive()
    }

    fun getDeadlinesCurrent(): LiveData<List<Deadline>> {
        return deadlineDAO.getAllCurrentLive()
    }

    fun findItem(name: String) {
        launch(Dispatchers.Main) {
            foundData.value = withContext(Dispatchers.IO) {
                deadlineDAO.findDeadline(name)
            }
        }
    }

    fun insertDeadline(deadline: Deadline) {
        launch(Dispatchers.IO) {
            deadlineDAO.insert(deadline)
        }
    }

    fun deleteDeadline(deadline: Deadline) {
        launch(Dispatchers.IO) {
            deadlineDAO.delete(deadline)
        }
    }

    fun updateDeadline(deadline: Deadline) {
        launch(Dispatchers.IO) {
            deadlineDAO.update(deadline)
        }
    }

    fun cancel() {
        job.cancel()
    }
}