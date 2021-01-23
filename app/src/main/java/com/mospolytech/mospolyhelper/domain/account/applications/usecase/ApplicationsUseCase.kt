package com.mospolytech.mospolyhelper.domain.account.applications.usecase

import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class ApplicationsUseCase(
    private val repository: ApplicationsRepository
) {
    suspend fun getInfo(): Flow<Result<List<Application>>> =
        repository.getApplications().onStart {
            emit(Result.loading())
        }
    suspend fun getLocalInfo(): Flow<Result<List<Application>>> =
        repository.getLocalInfo().onStart {
            //emit(Result.loading())
        }

}