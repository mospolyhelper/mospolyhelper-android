package com.mospolytech.mospolyhelper.domain.account.dialogs.usecase

import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogsModel
import com.mospolytech.mospolyhelper.domain.account.dialogs.repository.DialogsRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class DialogsUseCase(private val repository: DialogsRepository) {

    suspend fun getInfo(): Flow<Result<DialogsModel>> =
        repository.getDialogs().onStart {
            emit(Result.loading())
        }
    suspend fun getLocalInfo(): Flow<Result<DialogsModel>> =
        repository.getLocalDialogs().onStart {
            //emit(Result.loading())
        }
}