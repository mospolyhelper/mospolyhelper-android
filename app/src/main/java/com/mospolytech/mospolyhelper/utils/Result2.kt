package com.mospolytech.mospolyhelper.utils

import kotlin.Result

sealed class Result2<out T> {
    class Success<T>(val value: T) : Result2<T>()
    class Failure<T>(val exception: Throwable) : Result2<T>()
    class Loading<T> : Result2<T>()
}


fun <T> Result2<T>.getOrNull(): T? =
    (this as? Result2.Success)?.value

fun <T> Result2<T>.exceptionOrNull(): Throwable? =
    (this as? Result2.Failure)?.exception

fun <R, T : R> Result2<T>.getOrDefault(defaultValue: R): R {
    return if (this is Result2.Success) {
        this.value
    } else {
        defaultValue
    }
}

fun <R, T> Result2<T>.mapSuccess(transform: (value: T) -> R): Result2<R> {
    return when(this) {
        is Result2.Success -> Result2.Success(transform(value))
        is Result2.Failure -> Result2.Failure(this.exception)
        is Result2.Loading -> Result2.Loading()
    }
}

fun <T> Result2<T>.mapFailure(transform: (exception: Throwable) -> Throwable): Result2<T> {
    return when(this) {
        is Result2.Success -> this
        is Result2.Failure -> Result2.Failure(transform(this.exception))
        is Result2.Loading -> this
    }
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
fun <R> runCatching(block: () -> R): Result2<R> {
    return try {
        Result2.Success(block())
    } catch (e: Throwable) {
        Result2.Failure(e)
    }
}

/**
 * Calls the specified function [block] with `this` value as its receiver and returns its encapsulated result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
fun <T, R> T.runCatching(block: T.() -> R): Result2<R> {
    return try {
        Result2.Success(block())
    } catch (e: Throwable) {
        Result2.Failure(e)
    }
}