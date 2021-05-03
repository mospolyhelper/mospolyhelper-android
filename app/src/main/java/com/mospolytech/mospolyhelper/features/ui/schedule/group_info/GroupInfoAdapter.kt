package com.mospolytech.mospolyhelper.features.ui.schedule.group_info

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class GroupInfoAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    enum class Fragments {
        Schedule,
        Students
    }

    override fun getItemCount(): Int = 2//Fragments.values().size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            Fragments.Schedule.ordinal -> ScheduleFragment()
            Fragments.Students.ordinal -> ScheduleFragment()
            else -> throw NotImplementedError()
        }
    }
}
