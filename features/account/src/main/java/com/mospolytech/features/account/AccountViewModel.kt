package com.mospolytech.features.account

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountViewModel(
    private val navController: NavController
) : ViewModel() {
    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()
}

data class AccountState(val id: Int = 0) : State<AccountState.Mutator> {
    inner class Mutator : BaseMutator<AccountState>(this) { }
    override fun mutator() = Mutator()
}