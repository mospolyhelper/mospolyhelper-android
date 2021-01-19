package com.mospolytech.mospolyhelper.features.ui.account.messaging

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.usecase.ClassmatesUseCase
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.usecase.InfoUseCase
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.model.MessageSend
import com.mospolytech.mospolyhelper.domain.account.messaging.usecase.MessagingUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent

class MessagingViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: MessagingUseCase
) : ViewModelBase(mediator, MessagingViewModel::class.java.simpleName), KoinComponent {

    val dialog = MutableStateFlow<Result<List<Message>>>(Result.loading())

    val message = MutableStateFlow<Result<Message>>(Result.loading())

    private var dialogId: String = ""

    fun setDialogId(id: String) {
        dialogId = id
    }

    suspend fun getDialog() {
        useCase.getLocalDialog(dialogId).collect {
            dialog.value = it
        }
        dialog.value = Result.loading()
        useCase.getDialog(dialogId).collect {
            dialog.value = it

        }
    }

    suspend fun sendMessage(message: String, fileNames: List<String> = emptyList()) {
        useCase.sendMessage(dialogId, message, fileNames).collect {
            this.message.value = it
        }
    }

}