package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.domain.account.model.Order
import com.mospolytech.domain.account.model.Personal
import com.mospolytech.domain.account.repository.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PersonalRepositoryImpl(val api: AccountService): PersonalRepository {
    override fun getPersonalInfo(): Flow<Result<Personal>> = flow {
        emit(api.getPersonalInfo().toResult())
    }

    override fun getOrders(): Flow<Result<List<Order>>> = flow {
        emit(api.getOrders().toResult())
    }

}