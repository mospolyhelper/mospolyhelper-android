package com.mospolytech.mospolyhelper.features.ui.common

import androidx.lifecycle.ViewModel


abstract class ViewModelBase() : ViewModel() {
    private var mediator: Mediator<String, ViewModelMessage>? = null
    private var name: String? = null

    @Deprecated("Deprecated constructor")
    constructor(mediator: Mediator<String, ViewModelMessage>, name: String) : this() {
        this.mediator = mediator
        this.name = name
    }

    protected fun subscribe(block: (ViewModelMessage) -> Unit) {
        name?.let {
            mediator?.subscribe(it, block)
        }
    }

    private fun unsubscribe() {
        name?.let {
            mediator?.unsubscribe(it)
        }
    }

    protected fun send(target: String, key: String, vararg messages: Any?) {
        mediator?.send(target, ViewModelMessage(key, messages))
    }

    override fun onCleared() {
        unsubscribe()
        super.onCleared()
    }
}