package com.mospolytech.mospolyhelper.domain.account.repository

import com.mospolytech.mospolyhelper.domain.account.model.dialogs.DialogModel
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface DialogsRepository {

    suspend fun getDialogs(emitLocal: Boolean = true): Flow<Result0<List<DialogModel>>>

}