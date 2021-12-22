package com.mospolytech.domain.account.repository

import com.mospolytech.domain.account.model.Application
import kotlinx.coroutines.flow.Flow

interface ApplicationsRepository {
    fun getApplications(): Flow<Result<List<Application>>>
}