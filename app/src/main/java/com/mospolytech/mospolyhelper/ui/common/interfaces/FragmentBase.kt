package com.mospolytech.mospolyhelper.ui.common.interfaces

import androidx.fragment.app.Fragment
import com.mospolytech.mospolyhelper.ui.common.Fragments

interface IFragmentBase {
    val fragmentType: Fragments
    val fragment: Fragment
}