package com.mospolytech.mospolyhelper.domain.account.dialogs.usecase

import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.domain.account.dialogs.repository.DialogsRepository
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class DialogsUseCase(private val repository: DialogsRepository) {

    suspend fun getInfo(): Flow<Result2<List<DialogModel>>> =
        repository.getDialogs().onStart {
            emit(Result2.loading())
        }
    suspend fun getLocalInfo(): Flow<Result2<List<DialogModel>>> =
        repository.getLocalDialogs().onStart {
            //emit(Result2.loading())
        }

}