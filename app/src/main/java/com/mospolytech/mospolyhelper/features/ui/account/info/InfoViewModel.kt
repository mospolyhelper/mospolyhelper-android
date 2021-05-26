package com.mospolytech.mospolyhelper.features.ui.account.info

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.usecase.InfoUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class InfoViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: InfoUseCase
) : ViewModelBase(mediator, InfoViewModel::class.java.simpleName), KoinComponent {

    val info = MutableStateFlow<Result2<Info>>(Result2.loading())

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            info.value = it
        }
    }


    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            info.value = it
        }
        //info.value = Result2.loading()
        useCase.getInfo().collect {
            info.value = it
        }
    }

    fun getAvatar() = useCase.getAvatar()

}