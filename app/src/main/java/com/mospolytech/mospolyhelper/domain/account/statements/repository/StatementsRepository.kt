package com.mospolytech.mospolyhelper.domain.account.statements.repository

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface StatementsRepository {
    suspend fun getInfo(semester: String?): Flow<Result<Statements>>
    suspend fun getLocalInfo(): Flow<Result<Statements>>
}