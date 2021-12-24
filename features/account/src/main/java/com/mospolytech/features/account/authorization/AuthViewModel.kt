package com.mospolytech.features.account.authorization

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.repository.PeoplesRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.launch

class AuthViewModel() :
    BaseViewModel<AuthState, AuthMutator>(AuthState(), AuthMutator()) {
        init {

        }

}

data class AuthState(
   val auth: Boolean = false
)

class AuthMutator : BaseMutator<AuthState>() {

}