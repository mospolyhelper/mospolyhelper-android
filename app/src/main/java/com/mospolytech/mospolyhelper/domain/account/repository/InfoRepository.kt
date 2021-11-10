package com.mospolytech.mospolyhelper.domain.account.repository

import com.mospolytech.mospolyhelper.domain.account.model.info.Info
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface InfoRepository {
    suspend fun getInfo(emitLocal: Boolean = true): Flow<Result0<Info>>
}