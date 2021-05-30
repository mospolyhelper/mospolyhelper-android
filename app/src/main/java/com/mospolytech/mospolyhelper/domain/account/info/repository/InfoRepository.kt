package com.mospolytech.mospolyhelper.domain.account.info.repository

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface InfoRepository {
    suspend fun getInfo(): Flow<Result2<Info>>
    suspend fun getLocalInfo(): Flow<Result2<Info>>
    fun getAvatar(): String
}