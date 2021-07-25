package com.mospolytech.mospolyhelper.features.ui.account.group_marks

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheetMark
import com.mospolytech.mospolyhelper.domain.account.group_marks.usecase.GroupMarksUseCase
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class GroupMarksViewModel(
    private val groupMarksUseCase: GroupMarksUseCase,
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
        groupMarksUseCase.getLocalGradeSheet(guid).collect {
            gradeSheet.value = it
        }
        groupMarksUseCase.getGradeSheet(guid).collect {
            gradeSheet.value = it
        }
    }

    suspend fun getMarks(guid: String) {
        groupMarksUseCase.getLocalGradeSheetMarks(guid).collect {
            gradeSheetMarks.value = it
        }
        groupMarksUseCase.getGradeSheetMarks(guid).collect {
            gradeSheetMarks.value = it
        }
    }

    suspend fun download(guid: String) {
        groupMarksUseCase.getGradeSheet(guid).collect {
            gradeSheet.value = it
        }
        groupMarksUseCase.getGradeSheetMarks(guid).collect {
            gradeSheetMarks.value = it
        }
    }

}