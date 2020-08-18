package com.mospolytech.mospolyhelper.features.ui.common

class Mediator<TKey, TMessage> {
    private val subscribers: MutableMap<TKey, (TMessage) -> Unit> = mutableMapOf()
    private val notReceivedMessages: MutableMap<TKey, MutableList<TMessage>> = mutableMapOf()

    fun send(key: TKey, value: TMessage) {
        val receiver = subscribers[key]
        if (receiver != null) {
            receiver(value)
        } else {
            val list = notReceivedMessages[key]
            if (list == null) {
                notReceivedMessages[key] = mutableListOf(value)
            } else {
                list.add(value)
            }
        }
    }

    fun subscribe(key: TKey, block: (TMessage) -> Unit) {
        subscribers[key] = block
        notReceivedMessages[key]?.forEach(block)
    }

    fun unsubscribe(key: TKey) {
        subscribers.remove(key)
    }
}