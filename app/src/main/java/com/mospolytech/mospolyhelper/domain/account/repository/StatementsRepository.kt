package com.mospolytech.mospolyhelper.domain.account.repository

import com.mospolytech.mospolyhelper.domain.account.model.statements.Statements
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface StatementsRepository {
    suspend fun getStatements(semester: String?, emitLocal: Boolean): Flow<Result0<Statements>>
}