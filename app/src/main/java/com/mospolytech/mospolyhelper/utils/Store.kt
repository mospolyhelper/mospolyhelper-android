package com.mospolytech.mospolyhelper.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

abstract class Store<State, Intent, Event>(
    state: State
) {
    val scope: CoroutineScope = StoreCoroutineScope

    private val _state = MutableStateFlow(StatePair(null, state))
    private val _events = MutableSharedFlow<Event>()

    var state: State
        get() = _state.value.new
        set(value) {
            _state.value = _state.value.updated(value)
        }

    val statesFlow: Flow<StatePair<State>> = _state
    val eventsFlow: Flow<Event> = _events

    abstract fun onIntent(intent: Intent)

    private object StoreCoroutineScope : CoroutineScope {
        override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
    }
}

data class StatePair<State>(
    val old: State?,
    val new: State
) {
    fun updated(newState: State): StatePair<State> {
        return StatePair(
            old = this.new,
            new = newState
        )
    }

    inline fun <T> isChanged(propSelector: State.() -> T) =
        old == null || propSelector(old) !== propSelector(new)

    inline fun <T> isAnyChanged(propSelector: State.() -> List<T>) =
        old != null && isNotEqual(propSelector(old), propSelector(new))

    fun <T> isNotEqual(l1: List<T>, l2: List<T>): Boolean {
        if (l1.size != l2.size) throw IllegalStateException("Lists of properties must have equal size")
        l1.forEachIndexed { index, t ->
            if (t !== l2[index]) return true
        }
        return false
    }
}

fun <T, Y, Z> Store<T, Y, Z>.boundWith(scope: CoroutineScope) : Store<T, Y, Z> {
    scope.launch {
        suspendCancellableCoroutine {
            it.invokeOnCancellation {
                this@boundWith.scope.cancel()
            }
        }
    }
    return this
}