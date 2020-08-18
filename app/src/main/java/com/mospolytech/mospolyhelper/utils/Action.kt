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

interface Event2<T1, T2> {
    fun addListener(block: (T1, T2) -> Unit)
    fun removeListener(block: (T1, T2) -> Unit)
    operator fun plusAssign(block: (T1, T2) -> Unit)
    operator fun minusAssign(block: (T1, T2) -> Unit)
}

interface Event3<T1, T2, T3> {
    fun addListener(block: (T1, T2, T3) -> Unit)
    fun removeListener(block: (T1, T2, T3) -> Unit)
    operator fun plusAssign(block: (T1, T2, T3) -> Unit)
    operator fun minusAssign(block: (T1, T2, T3) -> Unit)
}

interface Event4<T1, T2, T3, T4> {
    fun addListener(block: (T1, T2, T3, T4) -> Unit)
    fun removeListener(block: (T1, T2, T3, T4) -> Unit)
    operator fun plusAssign(block: (T1, T2, T3, T4) -> Unit)
    operator fun minusAssign(block: (T1, T2, T3, T4) -> Unit)
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

class Action2<T1, T2>: Event2<T1, T2> {
    private val list = LinkedHashSet<(T1, T2) -> Unit>()

    operator fun invoke(obj1: T1, obj2: T2) {
        for (e in list) {
            e(obj1, obj2)
        }
    }

    override fun addListener(block: (T1, T2) -> Unit) {
        list.add(block)
    }

    override fun removeListener(block: (T1, T2) -> Unit) {
        list.remove(block)
    }

    override operator fun plusAssign(block: (T1, T2) -> Unit) {
        list.add(block)
    }

    override operator fun minusAssign(block: (T1, T2) -> Unit) {
        list.remove(block)
    }
}

class Action3<T1, T2, T3>: Event3<T1, T2, T3> {
    private val list = LinkedHashSet<(T1, T2, T3) -> Unit>()

    operator fun invoke(obj1: T1, obj2: T2, obj3: T3) {
        for (e in list) {
            e(obj1, obj2, obj3)
        }
    }

    override fun addListener(block: (T1, T2, T3) -> Unit) {
        list.add(block)
    }

    override fun removeListener(block: (T1, T2, T3) -> Unit) {
        list.remove(block)
    }

    override operator fun plusAssign(block: (T1, T2, T3) -> Unit) {
        list.add(block)
    }

    override operator fun minusAssign(block: (T1, T2, T3) -> Unit) {
        list.remove(block)
    }
}

class Action4<T1, T2, T3, T4>: Event4<T1, T2, T3, T4> {
    private val list = LinkedHashSet<(T1, T2, T3, T4) -> Unit>()

    operator fun invoke(obj1: T1, obj2: T2, obj3: T3, obj4: T4) {
        for (e in list) {
            e(obj1, obj2, obj3, obj4)
        }
    }

    override fun addListener(block: (T1, T2, T3, T4) -> Unit) {
        list.add(block)
    }

    override fun removeListener(block: (T1, T2, T3, T4) -> Unit) {
        list.remove(block)
    }

    override operator fun plusAssign(block: (T1, T2, T3, T4) -> Unit) {
        list.add(block)
    }

    override operator fun minusAssign(block: (T1, T2, T3, T4) -> Unit) {
        list.remove(block)
    }
}