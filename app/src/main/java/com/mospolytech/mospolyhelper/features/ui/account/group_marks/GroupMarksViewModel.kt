package com.mospolytech.mospolyhelper.features.ui.account.group_marks

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheetMark
import com.mospolytech.mospolyhelper.domain.account.repository.GroupMarksRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class GroupMarksViewModel(
    private val repository: GroupMarksRepository,
    private val authUseCase: AuthUseCase
): ViewModel(), KoinComponent {

    val gradeSheet = MutableStateFlow<Result0<GradeSheet>>(Result0.Loading)
    val gradeSheetMarks = MutableStateFlow<Result0<List<GradeSheetMark>>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun getGradeSheet(guid: String) {
        repository.getGradeSheet(guid, emitLocal = true).collect {
            gradeSheet.value = it
        }
    }

    suspend fun getMarks(guid: String) {
        repository.getGradeSheetMarks(guid, emitLocal = true).collect {
            gradeSheetMarks.value = it
        }
    }

    suspend fun download(guid: String) {
        repository.getGradeSheet(guid, emitLocal = false).collect {
            gradeSheet.value = it
        }
        repository.getGradeSheetMarks(guid, emitLocal = false).collect {
            gradeSheetMarks.value = it
        }
    }

}