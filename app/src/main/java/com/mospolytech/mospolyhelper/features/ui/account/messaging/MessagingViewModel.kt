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

    suspend fun downloadDialog(dialogId: String) {
        useCase.getDialog(dialogId).collect {
            dialog.value = it
        }
    }

    suspend fun getDialog(dialogId: String) {
        useCase.getLocalDialog(dialogId).collect {
            dialog.value = it
        }
        useCase.getDialog(dialogId).collect {
            dialog.value = it
        }
    }

    suspend fun sendMessage(dialogId: String, message: String, fileNames: List<String> = emptyList()) {
        useCase.sendMessage(dialogId, message, fileNames).collect {
            this.dialog.value = it
        }
    }

    fun getName() = useCase.getName()

    fun getAvatar() = useCase.getAvatar()

}