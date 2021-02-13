package com.mospolytech.mospolyhelper.features.ui.account.statements

import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.usecase.MarksUseCase
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.domain.account.statements.usecase.StatementsUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent

class StatementsViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: StatementsUseCase
) : ViewModelBase(mediator, StatementsViewModel::class.java.simpleName), KoinComponent {

    val statements = MutableStateFlow<Result<Statements>>(Result.loading())

    suspend fun downloadInfo(semesters: String? = null) {
        useCase.getInfo(semesters).collect {
            statements.value = it
        }
    }

    suspend fun getInfo(semesters: String? = null) {
        useCase.getLocalInfo().collect {
            statements.value = it
        }
        //marks.value = Result.loading()
        useCase.getInfo(semesters).collect {
            statements.value = it
        }
    }

}