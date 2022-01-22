package com.mospolytech.features.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.features.base.navigation.core.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel<TState, TMutator : BaseMutator<TState>, TAction>(
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

    private val _action = MutableSharedFlow<TAction>(replay = 0)
    val action = _action.asSharedFlow()

    override fun getMutator(state: TState) = mutatorFactory().apply { this.state = state }

    override fun mutateState(mutate: TMutator.() -> Unit) {
        _state.value = getMutator(_state.value).apply(mutate).state
    }

    open fun exit() {
        router.exit()
    }

//    open fun navigateTo(screen: Screen) {
//        router.navigateTo(screen)
//    }
}