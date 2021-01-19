package com.mospolytech.mospolyhelper.domain.account.marks.usecase

import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class MarksUseCase(
    private val repository: MarksRepository
) {
    suspend fun getInfo(): Flow<Result<Marks>> =
        repository.getInfo().onStart {
            emit(Result.loading())
        }
    suspend fun getLocalInfo(): Flow<Result<Marks>> =
        repository.getLocalInfo().onStart {
            //emit(Result.loading())
        }

}