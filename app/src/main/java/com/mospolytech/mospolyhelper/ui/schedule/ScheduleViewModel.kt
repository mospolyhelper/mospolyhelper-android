package com.mospolytech.mospolyhelper.ui.schedule

import androidx.lifecycle.MutableLiveData
import com.mospolytech.mospolyhelper.repository.local.dao.ScheduleDao
import com.mospolytech.mospolyhelper.repository.models.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.ui.schedule.calendar.CalendarViewModel
import com.mospolytech.mospolyhelper.ui.schedule.lesson_info.LessonInfoViewModel
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class ScheduleViewModel : ViewModelBase(Mediator(), ScheduleViewModel::class.java.simpleName) {
    companion object {
        const val ResaveSchedule = "ResaveSchedule"
        const val ChangeFragment = "ChangeFragment"
        const val ChangeDate = "ChangeDate"
    }
    val dao = ScheduleDao()
    val schedule = MutableLiveData<Schedule?>(null)
    val date = MutableLiveData(Calendar.getInstance())
    val isSession = MutableLiveData(false)
    val groupTitle = MutableLiveData<String>("")
    var scheduleDownloaded = false
    val scheduleFilter = MutableLiveData(Schedule.Filter.default)
    var isAdvancedSearch = false
    var groupList = emptyList<String>()
    val showEmptyLessons = MutableLiveData(false)

    val beginDownloading: Event0 = Action0()
    val endDownloading: Event0 = Action0()

    init {
        subscribe(::handleMessage)
        getGroupList(true)

        isSession.observeForever {
            GlobalScope.launch {
                setUpSchedule(true)
            }
        }

        // TODO: Change
        showEmptyLessons.observeForever {
            schedule.value = schedule.value
        }

        scheduleFilter.observeForever {
            schedule.value = schedule.value
        }
    }

    fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            ResaveSchedule -> schedule.value?.let { dao.saveSchedule(it) }
            ChangeFragment -> {
                val fragment = (message.content as List<*>)[0] as ScheduleFragment
                // FragmentChanged?.Invoke(scheduleFragment);
            }
            ChangeDate -> {
                date.value = (message.content as List<*>)[0] as Calendar
            }
        }
    }

    fun updateSchedule() {
        GlobalScope.launch(Dispatchers.Main) {
            setUpSchedule(true, withoutIndicator = true)
        }
    }

    fun setUpSchedule(downloadNew: Boolean, notMainThread: Boolean = false, withoutIndicator: Boolean = false) =
        GlobalScope.launch(Dispatchers.Main) {
        if (!withoutIndicator) {
            (beginDownloading as Action0).invoke()
        }
        if (groupTitle.value!!.isEmpty()) {
            this@ScheduleViewModel.schedule.value =
                dao.getSchedule2(groupTitle.value!!, isSession.value!!, false)
                    .apply { scheduleDownloaded = true }
            return@launch
        }
        val schedule = dao.getSchedule2(groupTitle.value!!, isSession.value!!, downloadNew)
        if (schedule == null) {
            this@ScheduleViewModel.schedule.value = dao.getSchedule2(groupTitle.value!!, isSession.value!!, !downloadNew)
        } else {
            this@ScheduleViewModel.schedule.value = schedule
        }
        if (notMainThread) {
            GlobalScope.launch(Dispatchers.Main) {
                this@ScheduleViewModel.schedule.value =
                    dao.getSchedule2(groupTitle.value!!, isSession.value!!, false)
                        .apply { scheduleDownloaded = true }
            }
        } else {
            this@ScheduleViewModel.schedule.value =
                dao.getSchedule2(groupTitle.value!!, isSession.value!!, false)
                    .apply { scheduleDownloaded = true }
            (endDownloading as Action0).invoke()
        }
    }

    suspend fun getAdvancedSearchData(groupList: List<String>, onProgressChanged: (Int) -> Unit): ScheduleDao.SchedulePackList? {
        // dao.downloadProgressChanged += onProgressChanged
        return dao.getSchedules(groupList)
    }

    fun goHome() {
        date.value = Calendar.getInstance()
    }

    fun openCalendar() {
        send(CalendarViewModel::class.java.simpleName, CalendarViewModel.CalendarMode, listOf(
            schedule.value!!,
            scheduleFilter.value!!,
            date.value!!,
            isAdvancedSearch
        ))
    }

    fun openLessonInfo(lesson: Lesson, date: Calendar) {
        send(LessonInfoViewModel::class.java.simpleName, LessonInfoViewModel.LessonInfo, listOf(
            lesson,
            date
        ))
    }

    fun submitGroupTitle() {
        GlobalScope.launch {
            setUpSchedule(true)
        }
    }

    fun getGroupList(downloadNew: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            groupList = dao.getGroupList2(downloadNew)
        }
    }
}
