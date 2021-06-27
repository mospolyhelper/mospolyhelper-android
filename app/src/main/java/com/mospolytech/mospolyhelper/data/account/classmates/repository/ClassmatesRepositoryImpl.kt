package com.mospolytech.mospolyhelper.data.account.classmates.repository

import com.mospolytech.mospolyhelper.data.account.classmates.remote.ClassmatesRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ClassmatesRepositoryImpl(
    private val dataSource: ClassmatesRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : ClassmatesRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getInfo() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        res.onSuccess {
            prefDataSource.setObject(it, PreferenceKeys.Classmates)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo() = flow {
        prefDataSource.getObject<List<Classmate>>(PreferenceKeys.Classmates)?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)



}