package com.mospolytech.features.base

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.mospolytech.features.base.navigation.Screen
import com.mospolytech.features.base.utils.nav
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel<TState, TMutator : BaseMutator<TState>, TAction>(
    initialState: TState,
    private val mutator: TMutator
): ViewModel(), StateStore<TState, TMutator>, KoinComponent {

    protected val navController: NavController by inject()

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _action = MutableSharedFlow<TAction>(replay = 0)
    val action = _action.asSharedFlow()

    override fun getMutator(state: TState) = mutator.apply { this.state = state }

    override fun mutateState(mutate: TMutator.() -> Unit) {
        _state.value = getMutator(_state.value).apply(mutate).state
    }

    open fun navigateBack() {
        navController.popBackStack()
    }

    open fun navigateTo(screen: Screen) {
        navController.nav(screen)
    }
}