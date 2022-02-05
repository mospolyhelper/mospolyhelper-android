package com.mospolytech.features.account.authorization

import com.mospolytech.features.base.core.BaseMutator
import com.mospolytech.features.base.core.BaseViewModel

class AuthViewModel() :
    BaseViewModel<AuthState, AuthMutator, Nothing>(AuthState(), ::AuthMutator) {

    fun authorize(login: String, password: String) {
        mutateState {
            authorizeChange(true)
        }
    }

    fun back() {
        router.exit()
    }

}

data class AuthState(
   val auth: Boolean = false,
   val isLoading: Boolean = false,
   val name: String = "Дындин Александр Владимирович",
   val avatar: String = "https://e.mospolytech.ru/old/img/photos/upc_ea66854573a60ed7938e5ac57a32cc69_1562662162.jpg"
)

class AuthMutator : BaseMutator<AuthState>() {
    fun setLoading(isLoading: Boolean) {
        state = state.copy(isLoading = isLoading)
    }
    fun authorizeChange(isAuthorized: Boolean) {
        state = state.copy(isLoading = false, auth = isAuthorized)
    }
}