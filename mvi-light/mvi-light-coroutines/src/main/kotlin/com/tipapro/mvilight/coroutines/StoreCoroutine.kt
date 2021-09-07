package com.tipapro.mvilight.coroutines

import com.tipapro.mvilight.main.Store
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.coroutineContext
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
val <State : Any, Intent : Any, Action : Any> Store<State, Intent, Action>.statesFlow: Flow<State>
    get() = callbackFlow {
        trySend(state)
        addOnStateChanged(::trySend)
        awaitClose { removeOnStateChanged(::trySend) }
    }

@OptIn(ExperimentalCoroutinesApi::class)
val <State : Any, Intent : Any, Action : Any> Store<State, Intent, Action>.actionsFlow: Flow<Action>
    get() = callbackFlow {
        addOnAction(::trySend)
        awaitClose { removeOnAction(::trySend) }
    }

val <State : Any, Intent : Any, Action : Any> Store<State, Intent, Action>.scope: CoroutineScope
    get() = StoreCoroutineScope

private object StoreCoroutineScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
}

fun <X : Any, Y : Any, Z : Any> Store<X, Y, Z>.boundWith(scope: CoroutineScope) : Store<X, Y, Z> {
    scope.launch {
        suspendCancellableCoroutine {
            it.invokeOnCancellation {
                this@boundWith.scope.cancel()
            }
        }
    }
    return this
}