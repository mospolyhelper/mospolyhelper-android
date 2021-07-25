package com.mospolytech.mospolyhelper.domain.account.info.repository

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface InfoRepository {
    suspend fun getInfo(): Flow<Result0<Info>>
    suspend fun getLocalInfo(): Flow<Result0<Info>>
}