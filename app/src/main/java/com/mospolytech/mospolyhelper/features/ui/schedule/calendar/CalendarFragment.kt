package com.mospolytech.mospolyhelper.features.ui.schedule.calendar

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.button.MaterialButtonToggleGroup
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
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_schedule_day)
        val recyclerAdapter = CalendarThreeAdapter(
            viewModel.filteredSchedule.value.getOrNull()?.schedule
        )
        recyclerAdapter.dayClick += { date ->
            viewModel.date.value = date
            findNavController().safe { navigateUp() }
        }

        recyclerView.itemAnimator = null
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = recyclerAdapter

        recyclerView.scrollToPosition(
            recyclerAdapter.firstPosDate.until(viewModel.date.value, ChronoUnit.DAYS).toInt()
        )

        viewBinding.toolbarScheduleCalendar.btngroupScheduleCalendar.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }
            when (checkedId) {
                R.id.btn_schedule_calendar_one -> {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    val recyclerAdapter = CalendarOneAdapter(
                        viewModel.filteredSchedule.value.getOrNull()?.schedule
                    )
                    recyclerView.adapter = recyclerAdapter
                    recyclerAdapter.dayClick += { date ->
                        viewModel.date.value = date
                        findNavController().safe { navigateUp() }
                    }
                }
                R.id.btn_schedule_calendar_three -> {
                    recyclerView.layoutManager = GridLayoutManager(context, 3)
                    val recyclerAdapter = CalendarThreeAdapter(
                        viewModel.filteredSchedule.value.getOrNull()?.schedule
                    )
                    recyclerView.adapter = recyclerAdapter
                    recyclerAdapter.dayClick += { date ->
                        viewModel.date.value = date
                        findNavController().safe { navigateUp() }
                    }
                }
                R.id.btn_schedule_calendar_seven -> {
                    recyclerView.layoutManager = GridLayoutManager(context, 7)
                    val recyclerAdapter = CalendarSevenAdapter(
                        viewModel.filteredSchedule.value.getOrNull()?.schedule
                    )
                    recyclerView.adapter = recyclerAdapter
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
