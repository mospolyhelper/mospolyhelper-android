package com.tipapro.mvilight.main

import java.lang.ref.WeakReference
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.properties.Delegates

abstract class Store<State : Any, Intent : Any, Action : Any>(
    state: State
) {
    ///
    // State
    ///

    var state by Delegates.observable(state) { _, _, new ->
        notifyState(new)
    }
        private set

    private fun notifyState(state: State) {
        Logger.getLogger(Store::class.java.name).log(Level.INFO, "State: $state")
        onStateChanged.forEach {
            it.get()?.invoke(state)
        }
        onStateChanged.removeIf { it.isEnqueued }
    }

    private val onStateChanged: MutableList<WeakReference<(State) -> Unit>> = LinkedList()

    fun addOnStateChanged(onStateChanged: (State) -> Unit) {
        this.onStateChanged += WeakReference(onStateChanged)
    }
    fun removeOnStateChanged(onStateChanged: (State) -> Unit) {
        this.onStateChanged.removeIf { it.get() == onStateChanged }
    }


    ///
    // Intent
    ///
    fun sendIntent(intent: Intent) {
        Logger.getLogger(Store::class.java.name).log(Level.INFO, "Intent: $intent")
        val result = ResultStateImpl(state)
        result.processIntent(intent)
        state = result.state
    }
    protected abstract fun ResultState.processIntent(intent: Intent)


    ///
    // Action
    ///
    private val onAction: MutableList<WeakReference<(Action) -> Unit>> = LinkedList()

    fun addOnAction(action: (Action) -> Unit) {
        onAction += WeakReference(action)
    }
    fun removeOnAction(action: (Action) -> Unit) {
        onAction.removeIf { it.get() == action }
    }

    protected fun sendAction(action: Action) {
        Logger.getLogger(Store::class.java.name).log(Level.INFO, "Action: $action")
        onAction.forEach {
            it.get()?.invoke(action)
        }
        onAction.removeIf { it.isEnqueued }
    }


    protected abstract inner class ResultState(
        var state: State
    )

    private inner class ResultStateImpl(
        state: State
    ) : ResultState(state)
}