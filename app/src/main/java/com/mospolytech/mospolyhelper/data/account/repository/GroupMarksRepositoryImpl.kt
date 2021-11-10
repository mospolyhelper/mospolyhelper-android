package com.mospolytech.mospolyhelper.data.account.repository

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheetMark
import com.mospolytech.mospolyhelper.domain.account.repository.GroupMarksRepository
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GroupMarksRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
    ): GroupMarksRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getGradeSheet(guid: String, emitLocal: Boolean) = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<GradeSheet>("$guid-sheet")?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val res = api.getGradeSheet(guid)
            .onSuccess {
                prefDataSource.setObject(it, "$guid-sheet")
            }
        emit(res)
    }.flowOn(ioDispatcher)

    override fun getGradeSheetMarks(guid: String, emitLocal: Boolean) = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<List<GradeSheetMark>>("$guid-marks")?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val res = api.getGradeSheetMarks(guid)
            .onSuccess {
                prefDataSource.setObject(it, "$guid-marks")
            }
        emit(res)
    }.flowOn(ioDispatcher)

}