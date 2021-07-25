package com.mospolytech.mospolyhelper.domain.account.applications.repository

import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface ApplicationsRepository {
    suspend fun getApplications(): Flow<Result0<List<Application>>>
    suspend fun getLocalInfo(): Flow<Result0<List<Application>>>
}