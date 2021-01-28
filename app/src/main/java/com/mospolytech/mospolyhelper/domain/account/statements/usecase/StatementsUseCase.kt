package com.mospolytech.mospolyhelper.domain.account.statements.usecase

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.domain.account.statements.repository.StatementsRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class StatementsUseCase(
    private val repository: StatementsRepository
) {

    suspend fun getInfo(semester: String?): Flow<Result<Statements>> =
        repository.getInfo(semester).onStart {
            emit(Result.loading())
        }

    suspend fun getLocalInfo(): Flow<Result<Statements>> =
        repository.getLocalInfo()

}