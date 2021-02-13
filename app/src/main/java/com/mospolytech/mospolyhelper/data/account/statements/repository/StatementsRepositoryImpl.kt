package com.mospolytech.mospolyhelper.data.account.statements.repository

import com.mospolytech.mospolyhelper.data.account.marks.local.MarksLocalDataSource
import com.mospolytech.mospolyhelper.data.account.marks.remote.MarksRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.statements.local.StatementsLocalDataSource
import com.mospolytech.mospolyhelper.data.account.statements.remote.StatementsRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.domain.account.statements.repository.StatementsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class StatementsRepositoryImpl(
    private val dataSource: StatementsRemoteDataSource,
    private val localDataSource: StatementsLocalDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : StatementsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getInfo(semester: String?) = flow {
        val sessionId = prefDataSource.getString(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId, semester)
        if (res.isSuccess)
            with (res.value as Statements) {
                if (semester?.isEmpty() != false || semester == semesterList[0])
                    localDataSource.set(this)
            }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo(): Flow<Result<Statements>>{
        val statements = localDataSource.getJson()
        return flow {
                if (statements.isNotEmpty()) emit(localDataSource.get(statements))
            }.flowOn(ioDispatcher)

    }



}