package com.mospolytech.mospolyhelper.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result0<out T> {
    data class Success<out T>(val value: T) : Result0<T>()
    data class Failure(val exception: Throwable) : Result0<Nothing>()
    object Loading : Result0<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$value]"
            is Failure -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = this is Failure

    val isLoading: Boolean get() = this is Loading

    /**
     * Returns the encapsulated value if this instance represents [success][Result0.isSuccess] or `null`
     * if it is [failure][Result0.isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? =
        if (this is Success) value else null

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? =
        if (this is Failure) exception else null
}


/**
 * Updates value of [MutableStateFlow] if [Result] is of type [Success]
 */
inline fun <reified T> Result0<T>.updateOnSuccess(stateFlow: MutableStateFlow<T>) {
    if (this is Result0.Success) {
        stateFlow.value = value
    }
}


/**
 * Creates an instance of internal marker [Result0.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
@PublishedApi
@SinceKotlin("1.3")
internal fun createFailure0(exception: Throwable): Any =
    Result0.Failure(exception)

/**
 * Throws exception if the result is failure. This internal function minimizes
 * inlined bytecode for [getOrThrow] and makes sure that in the future we can
 * add some exception-augmenting logic here (if needed).
 */
@PublishedApi
@SinceKotlin("1.3")
internal fun Result0<*>.throwOnFailure() {
    if (this is Result0.Failure) throw exception
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R> runCatching0(block: () -> R): Result0<R> {
    return try {
        Result0.Success(block())
    } catch (e: Throwable) {
        Result0.Failure(e)
    }
}

/**
 * Calls the specified function [block] with `this` value as its receiver and returns its encapsulated result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <T, R> T.runCatching0(block: T.() -> R): Result0<R> {
    return try {
        Result0.Success(block())
    } catch (e: Throwable) {
        Result0.Failure(e)
    }
}

// -- extensions ---

/**
 * Returns the encapsulated value if this instance represents [success][Result0.isSuccess] or throws the encapsulated [Throwable] exception
 * if it is [failure][Result0.isFailure].
 *
 * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
fun <T> Result0<T>.getOrThrow(): T {
    return when (this) {
        is Result0.Success -> this.value
        is Result0.Failure -> throw exception
        is Result0.Loading -> throw Exception("Result is Loading")
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][Result0.isSuccess] or the
 * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result0.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
 *
 * This function is a shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T : R> Result0<T>.getOrElse(
    onFailure: (exception: Throwable) -> R,
    onLoading: () -> R
): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onLoading, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result0.Success -> this.value
        is Result0.Failure -> onFailure(this.exception)
        is Result0.Loading -> onLoading()
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][Result0.isSuccess] or the
 * [defaultValue] if it is [failure][Result0.isFailure].
 *
 * This function is a shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
fun <R, T : R> Result0<T>.getOrDefault(defaultValue: R): R {
    return if (this is Result0.Success) value else defaultValue
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][Result0.isSuccess]
 * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result0.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T> Result0<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R,
    onLoading: () -> R
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onLoading, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result0.Success -> onSuccess(this.value)
        is Result0.Failure -> onFailure(this.exception)
        is Result0.Loading -> onLoading()
    }
}

// transformation

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result0.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result0.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T> Result0<T>.map(transform: (value: T) -> R): Result0<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result0.Success -> Result0.Success(transform(value))
        is Result0.Failure -> this
        is Result0.Loading -> this
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result0.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result0.isFailure].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [map] for an alternative that rethrows exceptions from `transform` function.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T> Result0<T>.mapCatching(transform: (value: T) -> R): Result0<R> {
    return when (this) {
        is Result0.Success -> runCatching0 { transform(value as T) }
        is Result0.Failure -> this
        is Result0.Loading -> this
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][Result0.isFailure] or the
 * original encapsulated value if it is [success][Result0.isSuccess].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T : R> Result0<T>.recover(transform: (exception: Throwable) -> R): Result0<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result0.Failure -> Result0.Success(transform(exception))
        else -> this
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][Result0.isFailure] or the
 * original encapsulated value if it is [success][Result0.isSuccess].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <R, T : R> Result0<T>.recoverCatching(transform: (exception: Throwable) -> R): Result0<R> {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> runCatching0 { transform(exception) }
    }
}


/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [failure][Result0.isFailure].
 * Returns the original `Result` unchanged.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <T> Result0<T>.onFailure(action: (exception: Throwable) -> Unit): Result0<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    exceptionOrNull()?.let { action(it) }
    return this
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Result0.isSuccess].
 * Returns the original `Result` unchanged.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <T> Result0<T>.onSuccess(action: (value: T) -> Unit): Result0<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (this is Result0.Success) action(value)
    return this
}