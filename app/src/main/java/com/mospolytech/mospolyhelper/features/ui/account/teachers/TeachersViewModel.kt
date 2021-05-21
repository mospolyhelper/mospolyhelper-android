package com.mospolytech.mospolyhelper.features.ui.account.teachers

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.domain.account.teachers.usecase.TeachersUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

class TeachersViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: TeachersUseCase
) : ViewModelBase(mediator, TeachersViewModel::class.java.simpleName), KoinComponent {

    fun fetchTeachers(query: String): Flow<PagingData<Teacher>> {
        return useCase.getInfo(query).cachedIn(viewModelScope)
    }
}