package com.mospolytech.mospolyhelper.data.account.repository

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setResultObject
import com.mospolytech.mospolyhelper.domain.account.model.applications.Application
import com.mospolytech.mospolyhelper.domain.account.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ApplicationsRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : ApplicationsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getApplications(emitLocal: Boolean) = flow {
        if (emitLocal) {
            prefDataSource.getResultObject<List<Application>>(PreferenceKeys.Applications)?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val result = api.getApplications().also {
            prefDataSource.setResultObject(it, PreferenceKeys.Applications)
        }
        emit(result)
    }.flowOn(ioDispatcher)





}