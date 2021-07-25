package com.mospolytech.mospolyhelper.domain.account.applications.usecase

import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class ApplicationsUseCase(
    private val repository: ApplicationsRepository
) {
    suspend fun getInfo(): Flow<Result0<List<Application>>> =
        repository.getApplications().onStart {
            emit(Result0.Loading)
        }
    suspend fun getLocalInfo(): Flow<Result0<List<Application>>> =
        repository.getLocalInfo()

}