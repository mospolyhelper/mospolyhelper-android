package com.mospolytech.mospolyhelper.data.account.marks.repository

import com.mospolytech.mospolyhelper.data.account.marks.remote.MarksRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MarksRepositoryImpl(
    private val dataSource: MarksRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : MarksRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getInfo() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        res.onSuccess {
            prefDataSource.setObject(it)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo() = flow {
        prefDataSource.getObject<Marks>()?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)




}