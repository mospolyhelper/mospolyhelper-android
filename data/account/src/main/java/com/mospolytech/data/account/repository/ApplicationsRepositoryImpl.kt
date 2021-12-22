package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.domain.account.model.Application
import com.mospolytech.domain.account.repository.ApplicationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApplicationsRepositoryImpl(private val api: AccountService): ApplicationsRepository {
    override fun getApplications(): Flow<Result<List<Application>>> = flow {
        emit(api.getApplications().toResult())
    }
}