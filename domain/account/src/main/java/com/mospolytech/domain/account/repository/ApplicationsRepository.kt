package com.mospolytech.domain.account.repository

import com.mospolytech.domain.account.model.Application

interface ApplicationsRepository {
    fun getApplications(): List<Application>
}