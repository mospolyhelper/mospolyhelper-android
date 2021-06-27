package com.mospolytech.mospolyhelper.domain.account.group_marks.usecase

import com.mospolytech.mospolyhelper.domain.account.group_marks.repository.GroupMarksRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.onStart

class GroupMarksUseCase(private val repository: GroupMarksRepository) {

    fun getGradeSheet(guid: String) =
        repository.getGradeSheet(guid).onStart {
            emit(Result0.Loading)
        }

    fun getGradeSheetMarks(guid: String) =
        repository.getGradeSheetMarks(guid).onStart {
            emit(Result0.Loading)
        }

    fun getLocalGradeSheet(guid: String) =
        repository.getLocalGradeSheet(guid)

    fun getLocalGradeSheetMarks(guid: String) =
        repository.getLocalGradeSheetMarks(guid)
}