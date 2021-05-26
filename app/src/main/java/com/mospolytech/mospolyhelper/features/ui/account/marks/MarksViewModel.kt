package com.mospolytech.mospolyhelper.features.ui.account.marks

import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.usecase.MarksUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class MarksViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: MarksUseCase
) : ViewModelBase(mediator, MarksViewModel::class.java.simpleName), KoinComponent {

    val marks = MutableStateFlow<Result2<Marks>>(Result2.loading())

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            marks.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            marks.value = it
        }
        //marks.value = Result2.loading()
        useCase.getInfo().collect {
            marks.value = it
        }
    }

}