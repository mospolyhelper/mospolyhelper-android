package com.mospolytech.mospolyhelper.utils

import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage

class StaticDI {
    companion object {
        val viewModelMediator = Mediator<String, ViewModelMessage>()
    }
}