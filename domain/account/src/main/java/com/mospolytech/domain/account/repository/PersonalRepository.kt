package com.mospolytech.domain.account.repository

import com.mospolytech.domain.account.model.Order
import com.mospolytech.domain.account.model.Personal

interface PersonalRepository {
    fun getPersonalInfo(): Personal
    fun getOrders(): List<Order>
}