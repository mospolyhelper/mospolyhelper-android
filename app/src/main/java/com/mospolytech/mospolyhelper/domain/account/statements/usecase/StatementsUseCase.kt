package com.mospolytech.mospolyhelper.domain.account.statements.usecase

import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.domain.account.statements.repository.StatementsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class StatementsUseCase(
    private val repository: StatementsRepository
) {

    suspend fun getInfo(semester: String?): Flow<Result0<Statements>> =
        repository.getInfo(semester).onStart {
            emit(Result0.Loading)
        }

    suspend fun getLocalInfo(): Flow<Result0<Statements>> =
        repository.getLocalInfo()

}