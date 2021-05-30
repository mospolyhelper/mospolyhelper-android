package com.mospolytech.mospolyhelper.features.ui.account.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.domain.account.teachers.usecase.TeachersUseCase
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class TeachersViewModel(
    private val useCase: TeachersUseCase,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

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