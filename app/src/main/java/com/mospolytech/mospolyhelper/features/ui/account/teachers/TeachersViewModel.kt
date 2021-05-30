package com.mospolytech.mospolyhelper.features.ui.account.teachers

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.domain.account.students.usecase.StudentsUseCase
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.domain.account.teachers.usecase.TeachersUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

class TeachersViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: TeachersUseCase,
    private val authUseCase: AuthUseCase
) : ViewModelBase(mediator, TeachersViewModel::class.java.simpleName), KoinComponent {

    val auth = MutableStateFlow<Result<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    fun fetchTeachers(query: String): Flow<PagingData<Teacher>> {
        return useCase.getInfo(query).cachedIn(viewModelScope)
    }
}