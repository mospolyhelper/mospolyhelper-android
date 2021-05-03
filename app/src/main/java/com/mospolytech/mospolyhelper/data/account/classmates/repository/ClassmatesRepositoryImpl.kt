package com.mospolytech.mospolyhelper.data.account.classmates.repository

import com.mospolytech.mospolyhelper.data.account.classmates.local.ClassmatesLocalDataSource
import com.mospolytech.mospolyhelper.data.account.classmates.remote.ClassmatesRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ClassmatesRepositoryImpl(
    private val dataSource: ClassmatesRemoteDataSource,
    private val localDataSource: ClassmatesLocalDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : ClassmatesRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getInfo() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        if (res.isSuccess) localDataSource.set(res.value as List<Classmate>)
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo(): Flow<Result<List<Classmate>>>{
        val info = localDataSource.getJson()
        return flow {
                if (info.isNotEmpty()) emit(localDataSource.get(info))
            }.flowOn(ioDispatcher)

    }



}