package com.mospolytech.mospolyhelper.features.ui.schedule.calendar

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleCalendarBinding
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.utils.safe
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.temporal.ChronoUnit

class CalendarFragment : DialogFragment(R.layout.fragment_schedule_calendar) {

    private val viewModel by sharedViewModel<ScheduleViewModel>()
    private val viewBinding by viewBinding(FragmentScheduleCalendarBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().invalidateOptionsMenu()

        (activity as MainActivity).setSupportActionBar(viewBinding.toolbarScheduleCalendar.toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val recyclerAdapter = CalendarThreeAdapter(
            viewModel.filteredSchedule.value.getOrNull()?.schedule
        )
        recyclerAdapter.dayClick += { date ->
            viewModel.date.value = date
            findNavController().safe { navigateUp() }
        }

        viewBinding.recyclerScheduleDay.itemAnimator = null
        viewBinding.recyclerScheduleDay.layoutManager = GridLayoutManager(context, 3)
        viewBinding.recyclerScheduleDay.adapter = recyclerAdapter

        viewBinding.recyclerScheduleDay.scrollToPosition(
            recyclerAdapter.firstPosDate.until(viewModel.date.value, ChronoUnit.DAYS).toInt()
        )

        viewBinding.toolbarScheduleCalendar.btngroupScheduleCalendar.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }
            when (checkedId) {
                R.id.btn_schedule_calendar_three -> {
                    viewBinding.recyclerScheduleDay.layoutManager = GridLayoutManager(context, 3)
                    val recyclerAdapter = CalendarThreeAdapter(
                        viewModel.filteredSchedule.value.getOrNull()?.schedule
                    )
                    viewBinding.recyclerScheduleDay.adapter = recyclerAdapter
                    recyclerAdapter.dayClick += { date ->
                        viewModel.date.value = date
                        findNavController().safe { navigateUp() }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
