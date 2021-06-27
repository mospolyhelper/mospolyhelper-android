package com.mospolytech.mospolyhelper.domain.account.dialogs.repository

import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface DialogsRepository {

    suspend fun getDialogs(): Flow<Result0<List<DialogModel>>>

    suspend fun getLocalDialogs(): Flow<Result0<List<DialogModel>>>

}