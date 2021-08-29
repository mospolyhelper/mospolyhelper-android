package com.tipapro.mvilight.main

data class StatePair<State>(
    val old: State?,
    val new: State
)

/**
 * Compares property of old and new states. Property provides by [propSelector]
 *
 * @param propSelector function that selects property for comparing.
 */
inline fun <State, T> StatePair<State>.isChanged(propSelector: State.() -> T) =
    old == null
            || propSelector(old) !== propSelector(new)
            && propSelector(old) != propSelector(new)

/**
 * Compares properties of old and new states and
 * return if one of properties has changed.
 * Properties provides by [propSelector]
 *
 * @param propSelector function that selects list of properties for comparing.
 */
inline fun <State, T> StatePair<State>.isAnyChanged(propSelector: State.() -> List<T>) =
    old == null || isNotEqual(propSelector(old), propSelector(new))

@Suppress("SuspiciousEqualsCombination")
fun <T> isNotEqual(l1: List<T>, l2: List<T>): Boolean {
    if (l1.size != l2.size) throw IllegalStateException("Lists of properties must have equal size")
    l1.forEachIndexed { index, t ->
        if (t !== l2[index] && t != l2[index]) return true
    }
    return false
}

inline fun <State, T> StatePair<State>.onChanged(propSelector: State.() -> T, onChangedAction: (State) -> Unit) {
    if (
        old == null
        || propSelector(old) !== propSelector(new)
        && propSelector(old) != propSelector(new)
    ) {
        onChangedAction(this.new)
    }
}

inline fun <State, T> StatePair<State>.onAnyChanged(propSelector: State.() -> List<T>, onChangedAction: (State) -> Unit) {
    if (old == null || isNotEqual(propSelector(old), propSelector(new))) {
        onChangedAction(this.new)
    }
}