package com.mospolytech.mospolyhelper.domain.account.info.repository

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface InfoRepository {
    suspend fun getInfo(): Flow<Result<Info>>
}