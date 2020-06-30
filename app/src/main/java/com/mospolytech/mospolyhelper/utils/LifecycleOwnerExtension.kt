package com.mospolytech.mospolyhelper.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class LifecycleOwnerExtension {
    val LifecycleOwner.isActive: Boolean
        get() = this.lifecycle.currentState != Lifecycle.State.DESTROYED
}