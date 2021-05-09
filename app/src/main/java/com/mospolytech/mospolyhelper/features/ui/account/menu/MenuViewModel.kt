package com.mospolytech.mospolyhelper.features.ui.account.menu

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

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