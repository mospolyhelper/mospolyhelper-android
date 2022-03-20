package com.mospolytech.features.base.core.mvi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.features.base.core.navigation.core.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModelFull<TState, TMutator : BaseMutator<TState>, TAction>(
    initialState: TState,
    private val mutatorFactory: () -> TMutator
): ViewModel(), StateStore<TState, TMutator>, KoinComponent {

    val router: Router by inject()

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            state.collect {
                Log.d("STATE", it.toString())
            }
        }
    }

    override fun getMutator(initialState: TState) = mutatorFactory().apply {
        this.state = initialState
        mutatorSetup()
    }

    override fun mutateState(mutate: TMutator.() -> Unit) {
        _state.value = getMutator(_state.value).apply {
            mutationScope {
                mutate()
            }
        }.state
    }


    private val _action = MutableSharedFlow<TAction>(replay = 0)
    val action = _action.asSharedFlow()

    fun sendAction(action: TAction) {
        viewModelScope.launch {
            _action.emit(action)
        }
    }


    private var mutatorSetup: TMutator.() -> Unit = { }

    fun setupMutator(setup: MutatorObserver<TState, TMutator>.() -> Unit) {
        val observer = MutatorObserver<TState, TMutator>().apply(setup)
        mutatorSetup = {
            this.onStateChanged += {
                observer.notify(this)
            }
        }
    }

    open fun exit() {
        router.exit()
    }
}