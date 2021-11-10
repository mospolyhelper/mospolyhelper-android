package com.mospolytech.mospolyhelper.features.ui.account.marks

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.marks.Marks
import com.mospolytech.mospolyhelper.domain.account.repository.MarksRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class MarksViewModel(
    private val repository: MarksRepository,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val marks = MutableStateFlow<Result0<Marks>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo() {
        repository.getMarks(emitLocal = false).collect {
            marks.value = it
        }
    }

    suspend fun getInfo() {
        repository.getMarks(emitLocal = true).collect {
            marks.value = it
        }
    }

}