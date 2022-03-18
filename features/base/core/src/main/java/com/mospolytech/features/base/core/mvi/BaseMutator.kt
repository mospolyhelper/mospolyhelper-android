package com.mospolytech.features.base.core.mvi

import com.mospolytech.features.base.core.utils.Typed1Listener

abstract class BaseMutator<TState> {
    private var _state: TState? = null
    var state: TState
        get() = _state!!
        set(value) {
            val needChange = _state != value
            if (needChange) {
                val needInvoke = _state != null
                _state = value
                if (needInvoke) {
                    onStateChanged.forEach { it(value) }
                }
            }
        }

    protected inline fun <T> set(stateProperty: T, newValue: T, crossinline action: TState.(T) -> TState): Boolean {
        val isChanged = stateProperty != newValue
        if (isChanged) {
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

    private val onStateChanged: MutableList<Typed1Listener<TState>> = mutableListOf()

    var observer: MutatorObserver<TState>? = null

    init {
        onStateChanged += {
            observer?.notify(this)
        }
    }
}

class MutatorObserver<TState> {
    private var prevState: TState? = null

    fun notify(mutator: BaseMutator<TState>) {
        val newState = mutator.state

        singlePropertySet.forEach { (propSelector, onChanged) ->
            val newValue = propSelector.selector(newState)
            val prevValue = prevState?.let { prevState -> propSelector.selector(prevState)  }
            if (prevValue != newValue) {
                onChanged(mutator)
            }
        }

        multiplePropertiesSet.forEach { (propSelector, onChanged) ->
            val newValue = propSelector.selector(newState)
            val prevValue = prevState?.let { prevState -> propSelector.selector(prevState)  }
            if (prevValue != newValue) {
                onChanged(mutator)
            }
        }

        prevState = newState
    }

    fun forProp(propSelector: TState.() -> Any?): SinglePropertySelector<TState> {
        return SinglePropertySelector(propSelector)
    }

    fun forProps(propsSelector: TState.() -> List<Any?>): MultiplePropertiesSelector<TState> {
        return MultiplePropertiesSelector(propsSelector)
    }

    fun SinglePropertySelector<TState>.onChanged(onChanged: BaseMutator<TState>.() -> Unit) {
        singlePropertySet.add(SingleObserverBundle(this, onChanged))
    }

    fun MultiplePropertiesSelector<TState>.onChanged(onChanged: BaseMutator<TState>.() -> Unit) {
        multiplePropertiesSet.add(MultipleObserverBundle(this, onChanged))
    }

    private val singlePropertySet: MutableSet<SingleObserverBundle<TState>> = mutableSetOf()
    private val multiplePropertiesSet: MutableSet<MultipleObserverBundle<TState>> = mutableSetOf()

    private data class SingleObserverBundle<TState>(
        val propSelector: SinglePropertySelector<TState>,
        val onChanged: BaseMutator<TState>.() -> Unit
    )

    private data class MultipleObserverBundle<TState>(
        val propsSelector: MultiplePropertiesSelector<TState>,
        val onChanged: BaseMutator<TState>.() -> Unit
    )

    class SinglePropertySelector<TState>(
        val selector: TState.() -> Any?
    )

    class MultiplePropertiesSelector<TState>(
        val selector: TState.() -> List<Any?>
    )
}