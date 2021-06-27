package com.mospolytech.mospolyhelper.domain.account.group_marks.repository

import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheetMark
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface GroupMarksRepository {
    fun getGradeSheet(guid: String): Flow<Result0<GradeSheet>>
    fun getGradeSheetMarks(guid: String): Flow<Result0<List<GradeSheetMark>>>
    fun getLocalGradeSheet(guid: String): Flow<Result0<GradeSheet>>
    fun getLocalGradeSheetMarks(guid: String): Flow<Result0<List<GradeSheetMark>>>
}