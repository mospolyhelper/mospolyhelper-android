package com.mospolytech.domain.account.repository

import com.mospolytech.domain.account.model.Order
import com.mospolytech.domain.account.model.Personal
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    fun getPersonalInfo(): Flow<Result<Personal>>
    fun getOrders(): Flow<Result<List<Order>>>
}