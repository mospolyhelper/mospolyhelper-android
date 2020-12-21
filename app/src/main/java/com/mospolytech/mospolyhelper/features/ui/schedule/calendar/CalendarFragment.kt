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
import com.google.android.material.button.MaterialButtonToggleGroup
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.utils.safe
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.temporal.ChronoUnit

class CalendarFragment : DialogFragment() {

    private val viewModel by sharedViewModel<ScheduleViewModel>()

    private lateinit var buttonGroup: MaterialButtonToggleGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_calendar, container, false)
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
        buttonGroup = view.findViewById(R.id.btngroup_schedule_calendar)
        val colorTitle = requireContext().getColor(R.color.calendarTitle)
        val colorCurrentTitle = requireContext().getColor(R.color.calendarCurrentTitle)
        val recyclerAdapter = CalendarThreeAdapter(
            viewModel.filteredSchedule.value.getOrNull()?.schedule,
            viewModel.isAdvancedSearch,
            requireContext().getColor(R.color.calendarParagraph),
            requireContext().getColor(R.color.calendarTimeBackground),
            colorTitle,
            colorCurrentTitle
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

        buttonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }
            when (checkedId) {
                R.id.btn_schedule_calendar_one -> {
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    val recyclerAdapter = CalendarOneAdapter(
                        viewModel.filteredSchedule.value.getOrNull()?.schedule,
                        viewModel.isAdvancedSearch,
                        requireContext().getColor(R.color.calendarParagraph),
                        requireContext().getColor(R.color.calendarTimeBackground),
                        colorTitle,
                        colorCurrentTitle
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
                        viewModel.filteredSchedule.value.getOrNull()?.schedule,
                        viewModel.isAdvancedSearch,
                        requireContext().getColor(R.color.calendarParagraph),
                        requireContext().getColor(R.color.calendarTimeBackground),
                        colorTitle,
                        colorCurrentTitle
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
                        viewModel.filteredSchedule.value.getOrNull()?.schedule,
                        viewModel.isAdvancedSearch,
                        requireContext().getColor(R.color.calendarParagraph),
                        requireContext().getColor(R.color.calendarTimeBackground),
                        colorTitle,
                        colorCurrentTitle
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
