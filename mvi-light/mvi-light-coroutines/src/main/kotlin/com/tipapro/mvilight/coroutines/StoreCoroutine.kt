package com.tipapro.mvilight.coroutines

import com.tipapro.mvilight.main.Store
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

val <State : Any, Intent : Any, Action : Any> Store<State, Intent, Action>.statesFlow: Flow<State>
    get() = flow {
        this@statesFlow.addOnStateChanged {
            this@statesFlow.scope.launch {
                emit(it)
            }
        }
    }

val <State : Any, Intent : Any, Action : Any> Store<State, Intent, Action>.actionsFlow: Flow<Action>
    get() = flow {
        this@actionsFlow.addOnAction {
            this@actionsFlow.scope.launch {
                emit(it)
            }
        }
    }

val <State : Any, Intent : Any, Action : Any> Store<State, Intent, Action>.scope: CoroutineScope
    get() = StoreCoroutineScope

private object StoreCoroutineScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
}

fun <X : Any, Y : Any, Z : Any> Store<X, Y, Z>.boundWith(scope: CoroutineScope) : com.tipapro.mvilight.main.Store<X, Y, Z> {
    scope.launch {
        suspendCancellableCoroutine {
            it.invokeOnCancellation {
                this@boundWith.scope.cancel()
            }
        }
    }
    return this
}