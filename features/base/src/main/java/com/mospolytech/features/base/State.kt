package com.mospolytech.features.base

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

    protected inline fun <T> set(stateProperty: T, newValue: T, crossinline action: TState.(T) -> TState): Boolean {
        val isChanged = stateProperty == newValue
        if (!isChanged) {
            state = state.action(newValue)
        }
        return isChanged
    }

    inline fun Boolean.then(crossinline action: () -> Unit): Boolean {
        if (this) {
            action()
        }
        return this
    }
}



