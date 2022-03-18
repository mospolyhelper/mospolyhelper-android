package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.domain.account.model.Order
import com.mospolytech.domain.account.model.Personal
import com.mospolytech.domain.account.repository.PersonalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PersonalRepositoryImpl(
    private val api: AccountService
): PersonalRepository {
    override fun getPersonalInfo() =
        api.getPersonalInfo()
            .flowOn(Dispatchers.IO)

    override fun getOrders() =
        api.getOrders()
            .flowOn(Dispatchers.IO)
}