package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.domain.account.model.Application
import com.mospolytech.domain.account.repository.ApplicationsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ApplicationsRepositoryImpl(
    private val api: AccountService
): ApplicationsRepository {
    override fun getApplications() =
        api.getApplications()
            .flowOn(Dispatchers.IO)
}