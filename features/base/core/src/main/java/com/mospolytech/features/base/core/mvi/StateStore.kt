package com.mospolytech.features.base.core.mvi

interface StateStore<TState, TMutator : BaseMutator<*>> {
    fun getMutator(initialState: TState): TMutator
    fun mutateState(mutate: TMutator.() -> Unit)
}