package com.mospolytech.features.base.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

suspend fun<T> Flow<Result<T>>.execute(
    onStart: (() -> Unit)? = null,
    onSuccess: ((T) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null) {
    onStart?.invoke()
    collect {
        if (it.isSuccess)
            it.getOrNull()?.let { value -> onSuccess?.invoke(value) }
        else
            it.exceptionOrNull()?.let{ exception -> onError?.invoke(exception) }
    }
}

suspend fun<T> Flow<Result<T>>.executeResult(onStart: (() -> Unit)? = null, onExecute: ((Result<T>) -> Unit)? = null) {
    onStart?.invoke()
    collect {
        onExecute?.invoke(it)
    }
}
