package com.mospolytech.mospolyhelper.features.ui.account.messaging

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.usecase.ClassmatesUseCase
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.usecase.InfoUseCase
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.usecase.MessagingUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class MessagingViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: MessagingUseCase,
    private val authUseCase: AuthUseCase
) : ViewModelBase(mediator, MessagingViewModel::class.java.simpleName), KoinComponent {

    val dialog = MutableStateFlow<Result<List<Message>>>(Result.loading())
    val update = MutableStateFlow<Result<List<Message>>>(Result.loading())
    val auth = MutableStateFlow<Result<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadDialog(dialogId: String) {
        useCase.getDialog(dialogId).collect {
            update.value = it
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

    suspend fun deleteMessage(dialogId: String, removeKey: String) {
        useCase.deleteMessage(dialogId, removeKey).collect {
            this.update.value = it
        }
    }

    fun getName() = useCase.getName()

    fun getAvatar() = useCase.getAvatar()

}