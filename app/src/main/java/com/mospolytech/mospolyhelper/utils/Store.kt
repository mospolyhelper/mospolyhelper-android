package com.mospolytech.mospolyhelper.utils

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.CoroutineContext

abstract class Store<State, Intent, Event>(
    state: State
) {
    val scope: CoroutineScope = StoreCoroutineScope

    private val _state = MutableStateFlow(state)
    private val _events = MutableSharedFlow<Event>()

    var state: State
        get() = _state.value
        set(value) {
            _state.value = value
        }

    val statesFlow: Flow<State> = _state
    val eventsFlow: Flow<Event> = _events

    abstract fun onIntent(intent: Intent)

    private object StoreCoroutineScope : CoroutineScope {
        override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
    }
}

fun <T, Y, Z> Store<T, Y, Z>.boundWith(scope: CoroutineScope) : Store<T, Y, Z> {
    scope.launch {
        suspendCancellableCoroutine {
            it.invokeOnCancellation {
                Log.d(TAG, "Cancel 0")
                this@boundWith.scope.cancel()
                Log.d(TAG, "Cancel 1")
            }
        }
    }
    return this
}