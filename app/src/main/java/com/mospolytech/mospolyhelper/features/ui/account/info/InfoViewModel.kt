package com.mospolytech.mospolyhelper.features.ui.account.info

import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.usecase.InfoUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class InfoViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: InfoUseCase,
    private val authUseCase: AuthUseCase
) : ViewModelBase(mediator, InfoViewModel::class.java.simpleName), KoinComponent {

    val info = MutableStateFlow<Result<Info>>(Result.loading())
    val auth = MutableStateFlow<Result<String>?>(null)

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            info.value = it
        }
    }

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            info.value = it
        }
        //info.value = Result.loading()
        useCase.getInfo().collect {
            info.value = it
        }
    }

    fun getAvatar() = useCase.getAvatar()

}