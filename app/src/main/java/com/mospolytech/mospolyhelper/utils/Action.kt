package com.mospolytech.mospolyhelper.utils

import java.util.*
import kotlin.collections.LinkedHashSet

interface Event0 {
    fun addListener(block: () -> Unit)
    fun removeListener(block: () -> Unit)
    operator fun plusAssign(block: () -> Unit)
    operator fun minusAssign(block: () -> Unit)
}

interface Event1<T> {
    fun addListener(block: (T) -> Unit)
    fun removeListener(block: (T) -> Unit)
    operator fun plusAssign(block: (T) -> Unit)
    operator fun minusAssign(block: (T) -> Unit)
}

class Action0: Event0 {
    private val list = LinkedHashSet<() -> Unit>()

    operator fun invoke() {
        for (e in list) {
            e()
        }
    }

    override fun addListener(block: () -> Unit) {
        list.add(block)
    }

    override fun removeListener(block: () -> Unit) {
        list.remove(block)
    }

    override operator fun plusAssign(block: () -> Unit) {
        list.add(block)
    }

    override operator fun minusAssign(block: () -> Unit) {
        list.remove(block)
    }
}

class Action1<T>: Event1<T> {
    private val list = LinkedList<(T) -> Unit>()

    operator fun invoke(obj: T) {
        for (e in list) {
            e(obj)
        }
    }

    override fun addListener(block: (T) -> Unit) {
        list.add(block)
    }

    override fun removeListener(block: (T) -> Unit) {
        list.remove(block)
    }

    override operator fun plusAssign(block: (T) -> Unit) {
        list.add(block)
    }

    override operator fun minusAssign(block: (T) -> Unit) {
        list.remove(block)
    }
}