package com.mospolytech.mospolyhelper.features.ui.account.statements

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.domain.account.statements.usecase.StatementsUseCase
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class StatementsViewModel(
    private val useCase: StatementsUseCase,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val statements = MutableStateFlow<Result2<Statements>>(Result2.loading())
    val auth = MutableStateFlow<Result2<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo(semesters: String? = null) {
        useCase.getInfo(semesters).collect {
            statements.value = it
        }
    }

    suspend fun getInfo(semesters: String? = null) {
        useCase.getLocalInfo().collect {
            statements.value = it
        }
        useCase.getInfo(semesters).collect {
            statements.value = it
        }
    }

}