package com.mospolytech.mospolyhelper.data.account.repository

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.model.statements.Statements
import com.mospolytech.mospolyhelper.domain.account.repository.StatementsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class StatementsRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : StatementsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getStatements(semester: String?, emitLocal: Boolean) = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<Statements>()?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val res = api.getStatements(semester)
            .onSuccess {
                with (it) {
                    if (semester?.isEmpty() != false || semester == semesterList[0])
                        prefDataSource.setObject(it)
                }
            }
        emit(res)
    }.flowOn(ioDispatcher)

}