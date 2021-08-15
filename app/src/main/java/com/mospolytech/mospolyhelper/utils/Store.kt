package com.mospolytech.mospolyhelper.utils

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

abstract class Store<State, Intent, Event, Action>(
    state: State
) {
    val scope: CoroutineScope = StoreCoroutineScope

    private val _state = MutableStateFlow(state)
    private val _action = MutableSharedFlow<Action>()

    init {
        scope.launch(Dispatchers.Default) {
            _state.collect {
                Log.d("Store::State", it.toString())
            }
        }
    }

    var state: State
        get() = _state.value
        protected set(value) {
            _state.value = value
        }

    val statesFlow: Flow<State> = _state
    val actionsFlow: Flow<Action> = _action

    fun sendIntent(intent: Intent) {
        Log.d("Store::Intent", intent.toString())
        processIntent(intent)
    }
    protected abstract fun processIntent(intent: Intent)

    fun sendEvent(event: Event) {
        Log.d("Store::Intent", event.toString())
        processEvent(event)
    }

    protected abstract fun processEvent(event: Event)

    protected fun sendAction(action: Action) {
        scope.launch {
            _action.emit(action)
        }
    }

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

fun <T, X, Y, Z> Store<T, X, Y, Z>.boundWith(scope: CoroutineScope) : Store<T, X, Y, Z> {
    scope.launch {
        suspendCancellableCoroutine {
            it.invokeOnCancellation {
                this@boundWith.scope.cancel()
            }
        }
    }
    return this
}