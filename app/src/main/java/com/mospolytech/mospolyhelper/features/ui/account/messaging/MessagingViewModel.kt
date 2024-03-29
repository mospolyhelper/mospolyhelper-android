package com.mospolytech.mospolyhelper.features.ui.account.messaging

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.usecase.MessagingUseCase
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class MessagingViewModel(
    private val useCase: MessagingUseCase,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val dialog = MutableStateFlow<Result0<List<Message>>>(Result0.Loading)
    val update = MutableStateFlow<Result0<List<Message>>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

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

    fun getName(): String {
        val name = authUseCase.getName().orEmpty().substringBeforeLast(" ", "")
        return "${name.substringAfter(" ")} ${name.substringBefore(" ")}"

    }

    fun getAvatar(): String {
        return authUseCase.getAvatar()
            .orEmpty()
            .replace("https://e.mospolytech.ru/img/", "")
            .replace("photos/", "")
    }

}