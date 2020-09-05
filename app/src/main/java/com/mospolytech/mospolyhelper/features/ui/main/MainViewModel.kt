package com.mospolytech.mospolyhelper.features.ui.main

import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel


class MainViewModel: ViewModelBase(Mediator(), MainViewModel::class.java.simpleName) {
    var currentFragmentNavId: Int = -1

    fun changeShowEmptyLessons(showEmptyLessons: Boolean) {
        send(ScheduleViewModel::class.java.simpleName, "ShowEmptyLessons", showEmptyLessons)
    }

    fun changeShowColoredLessons(showColoredLessons: Boolean) {
        send(ScheduleViewModel::class.java.simpleName, "ShowColoredLessons", showColoredLessons)
    }
}