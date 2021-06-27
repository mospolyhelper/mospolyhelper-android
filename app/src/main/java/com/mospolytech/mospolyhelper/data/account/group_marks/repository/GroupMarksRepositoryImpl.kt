package com.mospolytech.mospolyhelper.data.account.group_marks.repository

import com.mospolytech.mospolyhelper.data.account.group_marks.remote.GroupMarksRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheetMark
import com.mospolytech.mospolyhelper.domain.account.group_marks.repository.GroupMarksRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GroupMarksRepositoryImpl(
    private val dataSource: GroupMarksRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
    ): GroupMarksRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getGradeSheet(guid: String) = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.getGradeSheet(sessionId, guid)
        res.onSuccess {
            prefDataSource.setObject(it, "$guid-sheet")
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override fun getGradeSheetMarks(guid: String) = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.getGradeSheetMarks(sessionId, guid)
        res.onSuccess {
            prefDataSource.setObject(it, "$guid-marks")
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override fun getLocalGradeSheet(guid: String) = flow {
        prefDataSource.getObject<GradeSheet>("$guid-sheet")?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)

    override fun getLocalGradeSheetMarks(guid: String) = flow {
        prefDataSource.getObject<List<GradeSheetMark>>("$guid-marks")?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)
}