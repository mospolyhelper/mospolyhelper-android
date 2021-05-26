package com.mospolytech.mospolyhelper.domain.account.statements.usecase

import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.domain.account.statements.repository.StatementsRepository
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class StatementsUseCase(
    private val repository: StatementsRepository
) {

    suspend fun getInfo(semester: String?): Flow<Result2<Statements>> =
        repository.getInfo(semester).onStart {
            emit(Result2.loading())
        }

    suspend fun getLocalInfo(): Flow<Result2<Statements>> =
        repository.getLocalInfo()

}