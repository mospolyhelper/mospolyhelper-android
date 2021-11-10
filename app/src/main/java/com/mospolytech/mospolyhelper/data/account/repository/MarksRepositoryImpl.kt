package com.mospolytech.mospolyhelper.data.account.repository

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.model.marks.Marks
import com.mospolytech.mospolyhelper.domain.account.repository.MarksRepository
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MarksRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : MarksRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getMarks(emitLocal: Boolean) = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<Marks>()?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val res = api.getMarks()
            .onSuccess {
                prefDataSource.setObject(it)
            }
        emit(res)
    }.flowOn(ioDispatcher)

}