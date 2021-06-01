package com.mospolytech.mospolyhelper.domain.account.applications.repository

import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface ApplicationsRepository {
    suspend fun getApplications(): Flow<Result2<List<Application>>>
    suspend fun getLocalInfo(): Flow<Result2<List<Application>>>
}