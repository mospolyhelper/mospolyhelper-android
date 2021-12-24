package com.mospolytech.features.base

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel<TState, TMutator : BaseMutator<TState>>(
    initialState: TState,
    private val mutator: TMutator
): ViewModel(), StateStore<TState, TMutator>, KoinComponent {

    protected val navController: NavController by inject()

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    override fun getMutator(state: TState) = mutator.apply { this.state = state }

    override fun mutateState(mutate: TMutator.() -> Unit) {
        _state.value = getMutator(_state.value).apply(mutate).state
    }
}