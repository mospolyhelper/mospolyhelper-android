package com.mospolytech.mospolyhelper.domain.account.applications.usecase

import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class ApplicationsUseCase(
    private val repository: ApplicationsRepository
) {
    suspend fun getInfo(): Flow<Result2<List<Application>>> =
        repository.getApplications().onStart {
            emit(Result2.loading())
        }
    suspend fun getLocalInfo(): Flow<Result2<List<Application>>> =
        repository.getLocalInfo().onStart {
            //emit(Result2.loading())
        }

}