package com.mospolytech.mospolyhelper.data.account.repository

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.model.classmates.Classmate
import com.mospolytech.mospolyhelper.domain.account.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ClassmatesRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : ClassmatesRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getClassmates(emitLocal: Boolean) = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<List<Classmate>>(PreferenceKeys.Classmates)?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val res = api.getClassmates()
            .onSuccess {
                prefDataSource.setObject(it, PreferenceKeys.Classmates)
            }
        emit(res)
    }.flowOn(ioDispatcher)

}