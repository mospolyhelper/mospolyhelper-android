package com.mospolytech.mospolyhelper.domain.account.statements.repository

import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface StatementsRepository {
    suspend fun getInfo(semester: String?): Flow<Result2<Statements>>
    suspend fun getLocalInfo(): Flow<Result2<Statements>>
}