package com.mospolytech.mospolyhelper.ui.common

class Mediator<TKey, TMessage> {
    private val subscribers: MutableMap<TKey, (TMessage) -> Unit> = mutableMapOf()

    fun send(key: TKey, value: TMessage) {
        subscribers[key]?.let { it(value) }
    }

    fun subscribe(key: TKey, block: (TMessage) -> Unit) {
        subscribers[key] = block
    }

    fun unsubscribe(key: TKey) {
        subscribers.remove(key)
    }
}