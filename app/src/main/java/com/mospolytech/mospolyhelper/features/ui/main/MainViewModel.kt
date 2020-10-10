package com.mospolytech.mospolyhelper.features.ui.main

import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import kotlinx.coroutines.flow.MutableStateFlow


class MainViewModel: ViewModelBase(Mediator(), MainViewModel::class.java.simpleName) {
    var currentFragmentNavId = MutableStateFlow(-1)
}