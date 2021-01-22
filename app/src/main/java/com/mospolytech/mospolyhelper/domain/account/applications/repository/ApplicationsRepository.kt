package com.mospolytech.mospolyhelper.domain.account.applications.repository

import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface ApplicationsRepository {
    suspend fun getApplications(): Flow<Result<List<Application>>>
    suspend fun getLocalInfo(): Flow<Result<List<Application>>>
}