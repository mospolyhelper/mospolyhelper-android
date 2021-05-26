package com.mospolytech.mospolyhelper.features.ui.account.dialogs

import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.domain.account.dialogs.usecase.DialogsUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class DialogsViewModel(mediator: Mediator<String, ViewModelMessage>,
                       private val useCase: DialogsUseCase): ViewModelBase(mediator, DialogsViewModel::class.java.simpleName),
    KoinComponent {

    val dialogs = MutableStateFlow<Result2<List<DialogModel>>>(Result2.loading())

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            dialogs.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            dialogs.value = it
        }

        useCase.getInfo().collect {
            dialogs.value = it
        }
    }
}