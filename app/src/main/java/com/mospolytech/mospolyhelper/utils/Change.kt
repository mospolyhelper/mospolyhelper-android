package com.mospolytech.mospolyhelper.utils

sealed class Change<out T> {
    class Removed<T>(val removed: T) : Change<T>()
    class Edited<T>(val old: T, val new: T) : Change<T>()
    class Added<T>(val added: T) : Change<T>()
}