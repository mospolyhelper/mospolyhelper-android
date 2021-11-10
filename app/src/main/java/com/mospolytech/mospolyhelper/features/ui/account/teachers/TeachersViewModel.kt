package com.mospolytech.mospolyhelper.features.ui.account.teachers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.teachers.Teacher
import com.mospolytech.mospolyhelper.domain.account.repository.TeachersRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class TeachersViewModel(
    private val repository: TeachersRepository,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    fun fetchTeachers(query: String): Flow<PagingData<Teacher>> {
        return repository.getTeachers(query).cachedIn(viewModelScope)
    }
}