package com.mospolytech.mospolyhelper.features

import androidx.lifecycle.ViewModel
import com.mospolytech.features.base.core.BaseViewModel

class MainViewModel : BaseViewModel<Unit, Nothing, Nothing>(
    Unit,
    { error(Unit) }
) {
}