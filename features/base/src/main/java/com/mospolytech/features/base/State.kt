package com.mospolytech.features.base

interface State<TMutator : BaseMutator<*>> {
    fun mutator(): TMutator
}

abstract class BaseMutator<TState>(state: TState) {
    var state: TState = state
        protected set
}

@SuppressWarnings("UNCHECKED_CAST")
fun <TMutator : BaseMutator<*>, TState : State<TMutator>>
        TState.mutate(mutate: TMutator.() -> Unit): TState {
    return mutator().apply(mutate).state as TState
}