package com.mospolytech.mospolyhelper.domain.account.deadlines.usecase

import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.repository.DeadlinesRepository
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class DeadlinesUseCase(
    private val repository: DeadlinesRepository
) {
    suspend fun getInfo(): Flow<Result2<List<Deadline>>> =
        repository.getDeadlines().onStart {
            emit(Result2.loading())
        }
    suspend fun getLocalInfo(): Flow<Result2<List<Deadline>>> =
        repository.getLocalInfo().onStart {
            //emit(Result2.loading())
        }

    suspend fun setInfo(deadlines: List<Deadline>): Flow<Result2<List<Deadline>>> =
        repository.setDeadlines(deadlines).onStart {
            emit(Result2.loading())
        }
}