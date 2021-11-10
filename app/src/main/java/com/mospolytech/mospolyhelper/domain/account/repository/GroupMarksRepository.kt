package com.mospolytech.mospolyhelper.domain.account.repository

import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheetMark
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface GroupMarksRepository {
    fun getGradeSheet(guid: String, emitLocal: Boolean = true): Flow<Result0<GradeSheet>>
    fun getGradeSheetMarks(guid: String, emitLocal: Boolean = true): Flow<Result0<List<GradeSheetMark>>>
}