package com.mospolytech.mospolyhelper.features.ui.account.marks

import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.usecase.MarksUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent

class MarksViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: MarksUseCase,
    private val authUseCase: AuthUseCase
) : ViewModelBase(mediator, MarksViewModel::class.java.simpleName), KoinComponent {

    val marks = MutableStateFlow<Result<Marks>>(Result.loading())
    val auth = MutableStateFlow<Result<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            marks.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            marks.value = it
        }
        //marks.value = Result.loading()
        useCase.getInfo().collect {
            marks.value = it
        }
    }

}