package com.mospolytech.mospolyhelper.domain.account.repository

import com.mospolytech.mospolyhelper.domain.account.model.applications.Application
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface ApplicationsRepository {
    suspend fun getApplications(emitLocal: Boolean = true): Flow<Result0<List<Application>>>
}