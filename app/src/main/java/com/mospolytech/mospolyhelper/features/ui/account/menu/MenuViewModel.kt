package com.mospolytech.mospolyhelper.features.ui.account.menu

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

class MenuViewModel(
    private val useCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    suspend fun refresh(): Flow<Result0<String>> {
        return useCase.refresh()
    }

    fun getName() = useCase.getName()

    fun getPermissions() = useCase.getPermissions()

    fun getAvatar() = useCase.getAvatar()

}