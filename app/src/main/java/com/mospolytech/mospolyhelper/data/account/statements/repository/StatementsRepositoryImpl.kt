package com.mospolytech.mospolyhelper.data.account.statements.repository

import com.mospolytech.mospolyhelper.data.account.statements.remote.StatementsRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.domain.account.statements.repository.StatementsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class StatementsRepositoryImpl(
    private val dataSource: StatementsRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : StatementsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getInfo(semester: String?) = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId, semester)
        res.onSuccess {
            with (it) {
                if (semester?.isEmpty() != false || semester == semesterList[0])
                    prefDataSource.setObject(it)
            }
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo() = flow {
        prefDataSource.getObject<Statements>()?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)



}