package com.mospolytech.mospolyhelper.domain.core.model

sealed class Message<T> {
    abstract val text: T
}

class SuccessMessage<T>(
    override val text: T
) : Message<T>()

class WarningMessage<T>(
    override val text: T
    ) : Message<T>()

class ExceptionMessage<T>(
    override val text: T
) : Message<T>()