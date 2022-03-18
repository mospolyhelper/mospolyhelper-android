package com.mospolytech.mospolyhelper.features

import com.mospolytech.features.base.core.mvi.BaseViewModelFull

class MainViewModel : BaseViewModelFull<Unit, Nothing, Nothing>(
    Unit,
    { error(Unit) }
) {
}