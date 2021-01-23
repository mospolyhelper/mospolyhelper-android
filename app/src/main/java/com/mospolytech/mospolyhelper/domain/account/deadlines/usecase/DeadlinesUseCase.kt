package com.mospolytech.mospolyhelper.domain.account.deadlines.usecase

import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.repository.DeadlinesRepository
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class DeadlinesUseCase(
    private val repository: DeadlinesRepository
) {
    suspend fun getInfo(): Flow<Result<List<Deadline>>> =
        repository.getDeadlines().onStart {
            emit(Result.loading())
        }
    suspend fun getLocalInfo(): Flow<Result<List<Deadline>>> =
        repository.getLocalInfo().onStart {
            //emit(Result.loading())
        }

    suspend fun setInfo(deadlines: List<Deadline>): Flow<Result<List<Deadline>>> =
        repository.setDeadlines(deadlines).onStart {
            emit(Result.loading())
        }
}