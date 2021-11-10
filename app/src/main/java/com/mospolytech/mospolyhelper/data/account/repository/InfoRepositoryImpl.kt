package com.mospolytech.mospolyhelper.data.account.repository

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.model.info.Info
import com.mospolytech.mospolyhelper.domain.account.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class InfoRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : InfoRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getInfo(emitLocal: Boolean) = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<Info>()?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val res = api.getInfo()
            .onSuccess { info ->
                prefDataSource.setObject(info)
            }
        emit(res)
    }.flowOn(ioDispatcher)

}