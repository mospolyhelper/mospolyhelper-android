package com.mospolytech.domain.base.utils


class Lce<out T>(
    internal val result: Result<T>,
    /**
     * Returns `true` if this instance represents a loading outcome.
     */
    val isLoading: Boolean
) {
    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = result.isSuccess

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = result.isFailure
    

    // value & exception retrieval

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or `null`
     * if it is [failure][Result.isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? =
        result.getOrNull()

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? =
        result.exceptionOrNull()


    /**
     * Returns a string `Success(v)` if this instance represents [success][Result.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    override fun toString(): String =
        result.toString()

    // companion with constructors

    /**
     * Companion object for [Result] class that contains its constructor functions
     * [success] and [failure].
     */
    companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        fun <T> success(value: T, isLoading: Boolean = false): Lce<T> = 
            Lce(Result.success(value), isLoading)
            

        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         */
        fun <T> failure(exception: Throwable, isLoading: Boolean = false): Lce<T> =
            Lce(Result.failure<T>(exception), isLoading)
    }
}


/**
 * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or throws the encapsulated [Throwable] exception
 * if it is [failure][Result.isFailure].
 *
 * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
 */
fun <T> Lce<T>.getOrThrow(): T =
    this.result.getOrThrow()

/**
 * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or the
 * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
 *
 * This function is a shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
 */
fun <R, T : R> Lce<T>.getOrElse(onFailure: (exception: Throwable) -> R): R =
    this.result.getOrElse(onFailure)

/**
 * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or the
 * [defaultValue] if it is [failure][Result.isFailure].
 *
 * This function is a shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
fun <R, T : R> Lce<T>.getOrDefault(defaultValue: R): R =
    this.result.getOrDefault(defaultValue)

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][Result.isSuccess]
 * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
 */
fun <R, T> Lce<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R
): R =
    this.result.fold(onSuccess, onFailure)

// transformation

/**
 * Returns the encapsulated result of the given [transform] function applied, the encapsulated value
 * if this instance represents [success][Result.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
fun <R, T> Lce<T>.map(transform: (value: T) -> R): Lce<R> =
    Lce(this.result.map(transform), isLoading)

/**
 * Returns the encapsulated result of the given [transform] function applied, the encapsulated value
 * if this instance represents [success][Result.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [map] for an alternative that rethrows exceptions from `transform` function.
 */
fun <R, T> Lce<T>.mapCatching(transform: (value: T) -> R): Lce<R> =
    Lce(this.result.mapCatching(transform), isLoading)

/**
 * Returns the encapsulated result of the given [transform] function applied, the encapsulated [Throwable] exception
 * if this instance represents [failure][Result.isFailure] or the
 * original encapsulated value if it is [success][Result.isSuccess].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
fun <R, T : R> Lce<T>.recover(transform: (exception: Throwable) -> R): Lce<R> =
    Lce(this.result.recoverCatching(transform), isLoading)

/**
 * Returns the encapsulated result of the given [transform] function applied, the encapsulated [Throwable] exception
 * if this instance represents [failure][Result.isFailure] or the
 * original encapsulated value if it is [success][Result.isSuccess].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */ fun <R, T : R> Lce<T>.recoverCatching(transform: (exception: Throwable) -> R): Lce<R> =
    Lce(this.result.recoverCatching(transform), isLoading)

// "peek" onto value/exception and pipe

/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [failure][Result.isFailure].
 * Returns the original `Result` unchanged.
 */
fun <T> Lce<T>.onFailure(action: (exception: Throwable) -> Unit): Lce<T> {
    this.result.onFailure(action)
    return this
}
    

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Result.isSuccess].
 * Returns the original `Result` unchanged.
 */
fun <T> Lce<T>.onSuccess(action: (value: T) -> Unit): Lce<T> {
    this.result.onSuccess(action)
    return this
}
    

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Result.isSuccess].
 * Returns the original `Result` unchanged.
 */
fun <T> Lce<T>.onLoading(action: () -> Unit): Lce<T> {
    if (isLoading) action()
    return this
}

// -------------------
