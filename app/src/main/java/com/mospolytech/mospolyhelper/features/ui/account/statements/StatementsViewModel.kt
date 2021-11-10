package com.mospolytech.mospolyhelper.features.ui.account.statements

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.statements.Statements
import com.mospolytech.mospolyhelper.domain.account.repository.StatementsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class StatementsViewModel(
    private val repository: StatementsRepository,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val statements = MutableStateFlow<Result0<Statements>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo(semesters: String? = null) {
        repository.getStatements(semesters, emitLocal = false).collect {
            statements.value = it
        }
    }

    suspend fun getInfo(semesters: String? = null) {
        repository.getStatements(semesters, emitLocal = true).collect {
            statements.value = it
        }
    }

}