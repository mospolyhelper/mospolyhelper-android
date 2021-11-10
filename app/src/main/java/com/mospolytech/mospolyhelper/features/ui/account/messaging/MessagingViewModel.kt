package com.mospolytech.mospolyhelper.features.ui.account.messaging

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.dialog.Message
import com.mospolytech.mospolyhelper.domain.account.repository.MessagingRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class MessagingViewModel(
    private val repository: MessagingRepository,
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
        repository.getDialog(dialogId, emitLocal = false).collect {
            update.value = it
        }
    }

    suspend fun getDialog(dialogId: String) {
        repository.getDialog(dialogId, emitLocal = true).collect {
            dialog.value = it
        }
    }

    suspend fun sendMessage(dialogId: String, message: String, fileNames: List<String> = emptyList()) {
        repository.sendMessage(dialogId, message, fileNames).collect {
            this.dialog.value = it
        }
    }

    suspend fun deleteMessage(dialogId: String, removeKey: String) {
        repository.deleteMessage(dialogId, removeKey).collect {
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