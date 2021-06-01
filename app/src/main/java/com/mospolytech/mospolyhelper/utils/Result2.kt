@file:Suppress("UNCHECKED_CAST", "RedundantVisibilityModifier")

package com.mospolytech.mospolyhelper.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */


/**
 * A discriminated union that encapsulates a successful outcome with a value of type [T]
 * or a failure with an arbitrary [Throwable] exception.
 */
@Deprecated("Use Result0", ReplaceWith("Result0"))
class Result2<out T> @PublishedApi internal constructor(
    val value: Any?
) {
    // discovery

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = value !is Failure && value !is Loading

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    public val isFailure: Boolean get() = value is Failure

    /**
     * Returns `true` if this instance represents a loading outcome.
     * In this case [isSuccess] returns `false`.
     */
    @Deprecated("Use Result0 for loading state", ReplaceWith("Result0.Loading"))
    public val isLoading: Boolean get() = value is Loading

    // value & exception retrieval

    /**
     * Returns the encapsulated value if this instance represents [success][Result2.isSuccess] or `null`
     * if it is [failure][Result2.isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? =
        when {
            isFailure || isLoading -> null
            else -> value as T
        }

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    /**
     * Returns a string `Success(v)` if this instance represents [success][Result2.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    override fun toString(): String =
        when (value) {
            is Failure -> value.toString() // "Failure($exception)"
            else -> "Success($value)"
        }

    // companion with constructors

    /**
     * Companion object for [Result2] class that contains its constructor functions
     * [success] and [failure].
     */
    public companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        fun <T> success(value: T): Result2<T> =
            Result2(value)

        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         */
        fun <T> failure(exception: Throwable): Result2<T> =
            Result2(createFailure(exception))

        /**
         * Returns an instance of loading.
         */
        @Deprecated("Use Result0 for loading state", ReplaceWith("Result0.Loading"))
        fun <T> loading(): Result2<T> =
            Result2(getLoading())
    }

    internal class Failure(
        @JvmField
        val exception: Throwable
    ) {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }

    @Deprecated("Use Result0 for loading state", ReplaceWith("Result0.Loading"))
    internal object Loading
}

/**
 * Creates an instance of internal marker [Result2.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
@PublishedApi
@SinceKotlin("1.3")
internal fun createFailure(exception: Throwable): Any =
    Result2.Failure(exception)

/**
 * Return an instance of internal marker Loading class to
 * make sure that this class is not exposed in ABI.
 */
@Deprecated("Use Result0 for loading state", ReplaceWith("Result0.Loading"))
internal fun getLoading(): Any = Result2.Loading

/**
 * Throws exception if the result is failure. This internal function minimizes
 * /*inline*/d bytecode for [getOrThrow] and makes sure that in the future we can
 * add some exception-augmenting logic here (if needed).
 */
internal fun Result2<*>.throwOnFailure() {
    if (value is Result2.Failure) throw value.exception
}

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
public fun <R> runCatching(block: () -> R): Result2<R> {
    return try {
        Result2.success(block())
    } catch (e: Throwable) {
        Result2.failure(e)
    }
}

/**
 * Calls the specified function [block] with `this` value as its receiver and returns its encapsulated result if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
public fun <T, R> T.runCatching(block: T.() -> R): Result2<R> {
    return try {
        Result2.success(block())
    } catch (e: Throwable) {
        Result2.failure(e)
    }
}

// -- extensions ---

/**
 * Returns the encapsulated value if this instance represents [success][Result2.isSuccess] or throws the encapsulated [Throwable] exception
 * if it is [failure][Result2.isFailure].
 *
 * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
 */
fun <T> Result2<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

/**
 * Returns the encapsulated value if this instance represents [success][Result2.isSuccess] or the
 * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result2.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
 *
 * This function is a shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
 */
fun <R, T : R> Result2<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    return when (val exception = exceptionOrNull()) {
        null -> value as T
        else -> onFailure(exception)
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][Result2.isSuccess] or the
 * [defaultValue] if it is [failure][Result2.isFailure].
 *
 * This function is a shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
public fun <R, T : R> Result2<T>.getOrDefault(defaultValue: R): R {
    if (isFailure || isLoading) return defaultValue
    return value as T
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][Result2.isSuccess]
 * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result2.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
 */
fun <R, T> Result2<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R,
    onLoading: () -> R
): R {
    val exception = exceptionOrNull()
    return when {
        exception == null && isLoading -> onLoading()
        exception == null -> onSuccess(value as T)
        else -> onFailure(exception)
    }
}

// transformation

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result2.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result2.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
fun <R, T> Result2<T>.map(transform: (value: T) -> R): Result2<R> {
    return when {
        isSuccess -> Result2.success(transform(value as T))
        else -> Result2(value)
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result2.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result2.isFailure].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [map] for an alternative that rethrows exceptions from `transform` function.
 */
public fun <R, T> Result2<T>.mapCatching(transform: (value: T) -> R): Result2<R> {
    return when {
        isSuccess -> runCatching { transform(value as T) }
        else -> Result2(value)
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][Result2.isFailure] or the
 * original encapsulated value if it is [success][Result2.isSuccess].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
fun <R, T : R> Result2<T>.recover(transform: (exception: Throwable) -> R): Result2<R> {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> Result2.success(transform(exception))
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Throwable] exception
 * if this instance represents [failure][Result2.isFailure] or the
 * original encapsulated value if it is [success][Result2.isSuccess].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */

public fun <R, T : R> Result2<T>.recoverCatching(transform: (exception: Throwable) -> R): Result2<R> {
    val value = value // workaround for /*inline*/ classes BE bug
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> runCatching { transform(exception) }
    }
}

// "peek" onto value/exception and pipe

/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [failure][Result2.isFailure].
 * Returns the original `Result2` unchanged.
 */
fun <T> Result2<T>.onFailure(action: (exception: Throwable) -> Unit): Result2<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Result.isSuccess].
 * Returns the original `Result` unchanged.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
inline fun <T> Result2<T>.onSuccess(action: (value: T) -> Unit): Result2<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (isSuccess) action(value as T)
    return this
}

@Deprecated("Use Result0 for loading state", ReplaceWith("Result0.Loading"))
fun <T> Result2<T>.onLoading(action: () -> Unit): Result2<T> {
    if (isLoading) action()
    return this
}

// -------------------