package com.mospolytech.mospolyhelper.domain.account.dialogs.repository

import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogsModel
import kotlinx.coroutines.flow.Flow
import com.mospolytech.mospolyhelper.utils.Result

interface DialogsRepository {

    suspend fun getDialogs(): Flow<Result<DialogsModel>>

    suspend fun getLocalDialogs(): Flow<Result<DialogsModel>>

}