package com.mospolytech.mospolyhelper.ui.schedule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mospolytech.mospolyhelper.repository.dao.ScheduleDao
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
import java.time.LocalDate


class ScheduleViewModel(
    schedule: Schedule? = null,
    date: LocalDate? = null,
    isSession: Boolean? = null,
    groupTitle: String? = null,
    scheduleFilter: Schedule.Filter? = null,
    showEmptyLessons: Boolean? = null
) : ViewModelBase(Mediator(), ScheduleViewModel::class.java.simpleName) {
    companion object {
        const val ResaveSchedule = "ResaveSchedule"
        const val ChangeFragment = "ChangeFragment"
        const val ChangeDate = "ChangeDate"
    }
    val dao = ScheduleDao()
    val schedule = MutableLiveData<Schedule?>(schedule)
    val date = MutableLiveData(date ?: LocalDate.now())
    val isSession = MutableLiveData(isSession ?: false)
    val groupTitle = MutableLiveData<String>(groupTitle ?: "")
    var scheduleDownloaded = false
    val scheduleFilter = MutableLiveData(scheduleFilter ?: Schedule.Filter.default)
    var isAdvancedSearch = false
    var groupList = MutableLiveData(emptyList<String>())
    val showEmptyLessons = MutableLiveData(showEmptyLessons ?: false)

    val beginDownloading: Event0 = Action0()
    val endDownloading: Event0 = Action0()

    init {
        subscribe(::handleMessage)
        getGroupList(true)

//        this.isSession.observeForever {
//            GlobalScope.launch {
//                setUpSchedule(true)
//            }
//        }
//
//        // TODO: Change
//        this.showEmptyLessons.observeForever {
//            this.schedule.value = this.schedule.value
//        }
//
//        this.scheduleFilter.observeForever {
//            this.schedule.value = this.schedule.value
//        }
    }

    fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            ResaveSchedule -> schedule.value?.let { dao.saveSchedule(it) }
            ChangeFragment -> {
                val fragment = (message.content as List<*>)[0] as ScheduleFragment
                // FragmentChanged?.Invoke(scheduleFragment);
            }
            ChangeDate -> {
                date.value = (message.content as List<*>)[0] as LocalDate
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

            val schedule = if (groupTitle.value!!.isEmpty()) {
                null
            } else {
                dao.getSchedule2(groupTitle.value!!, isSession.value!!, downloadNew)
                    ?: dao.getSchedule2(groupTitle.value!!, isSession.value!!, !downloadNew)
            }
            scheduleDownloaded = true
            this@ScheduleViewModel.schedule.value = schedule
            (endDownloading as Action0).invoke()
        }

    suspend fun getAdvancedSearchData(groupList: List<String>, onProgressChanged: (Int) -> Unit): ScheduleDao.SchedulePackList? {
        // dao.downloadProgressChanged += onProgressChanged
        return dao.getSchedules(groupList)
    }

    fun goHome() {
        date.value = LocalDate.now()
    }

    fun openCalendar() {
        send(CalendarViewModel::class.java.simpleName, CalendarViewModel.CalendarMode, listOf(
            schedule.value!!,
            scheduleFilter.value!!,
            date.value!!,
            isAdvancedSearch
        ))
    }

    fun openLessonInfo(lesson: Lesson, date: LocalDate) {
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
            groupList.value = dao.getGroupList2(downloadNew)
        }
    }

    class Factory: ViewModelProvider.Factory {
        var schedule: Schedule? = null
        var date: LocalDate? = null
        var isSession: Boolean? = null
        var groupTitle: String? = null
        var scheduleFilter: Schedule.Filter? = null
        var showEmptyLessons: Boolean? = null

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass == ScheduleViewModel::class.java) {
                return ScheduleViewModel(
                    schedule,
                    date,
                    isSession,
                    groupTitle,
                    scheduleFilter,
                    showEmptyLessons
                ) as T
            } else {
                throw IllegalArgumentException("${modelClass.name} is not ${ScheduleViewModel::class.java.name}")
            }
        }
    }
}
