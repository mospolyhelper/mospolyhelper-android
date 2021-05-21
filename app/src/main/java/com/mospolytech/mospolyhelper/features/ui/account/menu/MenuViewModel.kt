package com.mospolytech.mospolyhelper.features.ui.account.menu

import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

class MenuViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: AuthUseCase
) : ViewModelBase(mediator, MenuViewModel::class.java.simpleName), KoinComponent {

    suspend fun refresh(): Flow<Result<String>> {
        return useCase.refresh()
    }

    fun getName() = useCase.getName()

    fun getPermissions() = useCase.getPermissions()

    fun getAvatar() = useCase.getAvatar()

}