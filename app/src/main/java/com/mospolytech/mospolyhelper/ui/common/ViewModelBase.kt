package com.mospolytech.mospolyhelper.ui.common

import androidx.lifecycle.ViewModel

abstract class ViewModelBase(
    private val mediator: Mediator<String, ViewModelMessage>,
    private val name: String
): ViewModel() {

    protected fun subscribe(block: (ViewModelMessage) -> Unit) {
        mediator.subscribe(name, block)
    }

    protected fun unsubscribe() {
        mediator.unsubscribe(name)
    }

    protected fun send(target: String, key: String, vararg messages: Any?) {
        mediator.send(target, ViewModelMessage(key, messages))
    }

    override fun onCleared() {
        unsubscribe()
        super.onCleared()
    }
}