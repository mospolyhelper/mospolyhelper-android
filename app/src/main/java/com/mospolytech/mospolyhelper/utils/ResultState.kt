package com.mospolytech.mospolyhelper.utils

sealed interface ResultState<out T> {
    @JvmInline
    value class Ready<out T>(val result: Result2<T>): ResultState<T>
    object Loading: ResultState<Nothing>
}

fun <T> Result2<T>.toState(): ResultState<T> {
    return ResultState.Ready(this)
}

fun <T> ResultState<T>.onReady(action: (result: Result2<T>) -> Unit): ResultState<T> {
    if (this is ResultState.Ready) action(this.result)
    return this
}

fun <T> ResultState<T>.onLoading(action: () -> Unit): ResultState<T> {
    if (this is ResultState.Loading) action()
    return this
}