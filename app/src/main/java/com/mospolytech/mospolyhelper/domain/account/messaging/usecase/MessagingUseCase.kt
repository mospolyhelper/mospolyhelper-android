package com.mospolytech.mospolyhelper.domain.account.messaging.usecase

import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.repository.MessagingRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class MessagingUseCase(
    private val repository: MessagingRepository
) {
    suspend fun getDialog(dialogKey: String): Flow<Result0<List<Message>>> =
        repository.getDialog(dialogKey).onStart {
            emit(Result0.Loading)
        }
    suspend fun getLocalDialog(dialogKey: String): Flow<Result0<List<Message>>> =
        repository.getLocalDialog(dialogKey).onStart {
        }

    suspend fun sendMessage(dialogKey: String, message: String, fileNames: List<String>): Flow<Result0<List<Message>>> =
        repository.sendMessage(dialogKey, message, fileNames).onStart {
            emit(Result0.Loading)
        }

    suspend fun deleteMessage(dialogKey: String, removeKey: String): Flow<Result0<List<Message>>> =
        repository.deleteMessage(dialogKey, removeKey).onStart {
            emit(Result0.Loading)
        }
}