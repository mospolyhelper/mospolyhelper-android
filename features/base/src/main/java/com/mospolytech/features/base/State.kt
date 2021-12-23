package com.mospolytech.features.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//interface State<TMutator : BaseMutator<*>> {
//    fun mutator(): TMutator
//}
//
//abstract class BaseMutator<TState>(state: TState) {
//    var state: TState = state
//        protected set
//}
//
//@SuppressWarnings("UNCHECKED_CAST")
//fun <TMutator : BaseMutator<*>, TState : State<TMutator>>
//        TState.mutate(mutate: TMutator.() -> Unit): TState {
//    return mutator().apply(mutate).state as TState
//}


interface StateStore<TState, TMutator : BaseMutator<*>> {
    fun getMutator(state: TState): TMutator
    fun mutateState(mutate: TMutator.() -> Unit)
}

//interface State

abstract class BaseMutator<TState> {
    private var _state: TState? = null
    var state: TState
        get() = _state!!
        set(value) {
            _state = value
        }

    protected inline fun <T> set(v: T, v2: T, crossinline action: TState.(T) -> TState) {
        if (v != v2) {
            state = state.action(v2)
        }
    }

    inline fun <T> T.then(crossinline action: () -> Unit) {
        action()
    }
}



