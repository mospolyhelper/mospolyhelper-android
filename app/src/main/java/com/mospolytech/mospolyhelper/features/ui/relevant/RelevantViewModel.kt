package com.mospolytech.mospolyhelper.features.ui.relevant

import com.mospolytech.mospolyhelper.domain.schedule.model.StudentScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import org.koin.core.component.KoinComponent

class RelevantViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: ScheduleUseCase
) : ViewModelBase(mediator, RelevantViewModel::class.java.simpleName), KoinComponent {
    fun getSchedule() = useCase.getSchedule(StudentScheduleSource("181-721", "181-721"))
}