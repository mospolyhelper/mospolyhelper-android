package com.mospolytech.mospolyhelper.ui.common

import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.mospolytech.mospolyhelper.ui.common.interfaces.IFragmentBase

abstract class FragmentBase(override val fragmentType: Fragments)
    : Fragment(), IFragmentBase {
    override val fragment: Fragment = this
}

abstract class FragmentPreferenceBase(override val fragmentType: Fragments)
    : PreferenceFragmentCompat(), IFragmentBase {
    override val fragment: Fragment = this
}

enum class Fragments {
    ScheduleMain,
    ScheduleManager,
    Settings,
    Addresses,
    Other,
    ScheduleLessonInfo,
    ScheduleCalendar,
    Deadlines
}