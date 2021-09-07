package com.tipapro.mvilight.main

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
            it.invoke(state)
        }
    }

    private val onStateChanged: MutableList<(State) -> Unit> = LinkedList()

    fun addOnStateChanged(onStateChanged: (State) -> Unit) {
        this.onStateChanged += onStateChanged
    }
    fun removeOnStateChanged(onStateChanged: (State) -> Unit) {
        this.onStateChanged.removeIf { it == onStateChanged }
    }


    ///
    // Intent
    ///
    fun sendIntent(intent: Intent) {
        Logger.getLogger(Store::class.java.name).log(Level.INFO, "Intent: ${intent.javaClass.simpleName}, $intent")
        val result = ResultStateImpl(state)
        result.processIntent(intent)
    }
    protected abstract fun ResultState.processIntent(intent: Intent)

    protected fun ResultState.commitState() {
        this@Store.state = this.state
    }


    ///
    // Action
    ///
    private val onAction: MutableList<(Action) -> Unit> = LinkedList()

    fun addOnAction(action: (Action) -> Unit) {
        onAction += action
    }
    fun removeOnAction(action: (Action) -> Unit) {
        onAction.removeIf { it == action }
    }

    protected fun sendAction(action: Action) {
        Logger.getLogger(Store::class.java.name).log(Level.INFO, "Action: $action")
        onAction.forEach {
            it.invoke(action)
        }
    }


    protected abstract inner class ResultState(
        var state: State
    )

    private inner class ResultStateImpl(
        state: State
    ) : ResultState(state)
}