package com.mospolytech.mospolyhelper.domain.account.dialogs.repository

import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface DialogsRepository {

    suspend fun getDialogs(): Flow<Result2<List<DialogModel>>>

    suspend fun getLocalDialogs(): Flow<Result2<List<DialogModel>>>

}