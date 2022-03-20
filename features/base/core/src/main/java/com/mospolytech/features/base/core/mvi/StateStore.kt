package com.mospolytech.features.base.core.mvi

interface StateStore<TState, TMutator : BaseMutator<TState>> {
    fun getMutator(initialState: TState): TMutator
    fun mutateState(mutate: TMutator.() -> Unit)
}