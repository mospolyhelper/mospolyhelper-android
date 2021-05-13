package com.mospolytech.mospolyhelper.domain.account.messaging.usecase

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.model.MessageSend
import com.mospolytech.mospolyhelper.domain.account.messaging.repository.MessagingRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class MessagingUseCase(
    private val repository: MessagingRepository
) {
    suspend fun getDialog(dialogKey: String): Flow<Result<List<Message>>> =
        repository.getDialog(dialogKey).onStart {
            emit(Result.loading())
        }
    suspend fun getLocalDialog(dialogKey: String): Flow<Result<List<Message>>> =
        repository.getLocalDialog(dialogKey).onStart {
            //emit(Result.loading())
        }

    suspend fun sendMessage(dialogKey: String, message: String, fileNames: List<String>): Flow<Result<Message>> =
        repository.sendMessage(dialogKey, message, fileNames).onStart {
            emit(Result.loading())
        }

    fun getName(): String {
        val name = repository.getName().substringBeforeLast(" ", "")
        val formattedName = "${name.substringAfter(" ")} ${name.substringBefore(" ")}"
        return formattedName
    }

    fun getAvatar(): String {
        var avatar = repository.getAvatar().replace("https://e.mospolytech.ru/img/", "")
        avatar = avatar.replace("photos/", "")
        return avatar
    }
}