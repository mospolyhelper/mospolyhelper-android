package com.mospolytech.mospolyhelper.domain.account.dialogs.usecase

import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.domain.account.dialogs.repository.DialogsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class DialogsUseCase(private val repository: DialogsRepository) {

    suspend fun getInfo(): Flow<Result0<List<DialogModel>>> =
        repository.getDialogs().onStart {
            emit(Result0.Loading)
        }
    suspend fun getLocalInfo(): Flow<Result0<List<DialogModel>>> =
        repository.getLocalDialogs()

}