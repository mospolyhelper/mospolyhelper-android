package com.mospolytech.mospolyhelper.features.ui.account.statements

import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.domain.account.statements.usecase.StatementsUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class StatementsViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: StatementsUseCase
) : ViewModelBase(mediator, StatementsViewModel::class.java.simpleName), KoinComponent {

    val statements = MutableStateFlow<Result2<Statements>>(Result2.loading())

    suspend fun downloadInfo(semesters: String? = null) {
        useCase.getInfo(semesters).collect {
            statements.value = it
        }
    }

    suspend fun getInfo(semesters: String? = null) {
        useCase.getLocalInfo().collect {
            statements.value = it
        }
        //marks.value = Result2.loading()
        useCase.getInfo(semesters).collect {
            statements.value = it
        }
    }

}