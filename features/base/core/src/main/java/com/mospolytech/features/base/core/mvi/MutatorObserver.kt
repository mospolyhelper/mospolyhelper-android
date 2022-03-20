package com.mospolytech.features.base.core.mvi

class MutatorObserver<TState, TMutator : BaseMutator<TState>> {
    private var prevState: TState? = null

    fun notify(mutator: TMutator) {
        singlePropertySet.forEach { (propSelector, onChanged) ->
            val newValue = propSelector.selector(mutator.state)
            val prevValue = prevState?.let { prevState -> propSelector.selector(prevState)  }
            if (prevValue != newValue) {
                mutator.mutationScope {
                    onChanged(mutator)
                }
            }
        }

        multiplePropertiesSet.forEach { (propSelector, onChanged) ->
            val newValue = propSelector.selector(mutator.state)
            val prevValue = prevState?.let { prevState -> propSelector.selector(prevState)  }
            if (prevValue != newValue) {
                mutator.mutationScope {
                    onChanged(mutator)
                }
            }
        }

        prevState = mutator.state
    }

    fun forProp(propSelector: TState.() -> Any?): SinglePropertySelector<TState> {
        return SinglePropertySelector(propSelector)
    }

    fun forProps(propsSelector: TState.() -> List<Any?>): MultiplePropertiesSelector<TState> {
        return MultiplePropertiesSelector(propsSelector)
    }

    fun SinglePropertySelector<TState>.onChanged(onChanged: TMutator.() -> Unit) {
        singlePropertySet.add(SingleObserverBundle(this, onChanged))
    }

    fun MultiplePropertiesSelector<TState>.onChanged(onChanged: TMutator.() -> Unit) {
        multiplePropertiesSet.add(MultipleObserverBundle(this, onChanged))
    }

    private val singlePropertySet: MutableSet<SingleObserverBundle<TState, TMutator>> = mutableSetOf()
    private val multiplePropertiesSet: MutableSet<MultipleObserverBundle<TState, TMutator>> = mutableSetOf()

    private data class SingleObserverBundle<TState, TMutator : BaseMutator<TState>>(
        val propSelector: SinglePropertySelector<TState>,
        val onChanged: TMutator.() -> Unit
    )

    private data class MultipleObserverBundle<TState, TMutator : BaseMutator<TState>>(
        val propsSelector: MultiplePropertiesSelector<TState>,
        val onChanged: TMutator.() -> Unit
    )

    class SinglePropertySelector<TState>(
        val selector: TState.() -> Any?
    )

    class MultiplePropertiesSelector<TState>(
        val selector: TState.() -> List<Any?>
    )
}