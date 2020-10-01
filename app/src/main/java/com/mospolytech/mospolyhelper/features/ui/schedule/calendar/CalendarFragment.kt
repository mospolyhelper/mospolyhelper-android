package com.mospolytech.mospolyhelper.features.ui.schedule.calendar

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.temporal.ChronoUnit

class CalendarFragment : DialogFragment() {

    private val viewModel by sharedViewModel<ScheduleViewModel>()

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
        val view = inflater.inflate(R.layout.fragment_schedule_calendar, container, false)
        requireActivity().invalidateOptionsMenu()
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_schedule_day)
        val colorTitle = requireContext().getColor(R.color.calendarTitle)
        val colorCurrentTitle = requireContext().getColor(R.color.calendarCurrentTitle)
        val recyclerAdapter = CalendarAdapter(
            viewModel.filteredSchedule.value.getOrNull()?.schedule,
            viewModel.isAdvancedSearch,
            requireContext().getColor(R.color.calendarParagraph),
            requireContext().getColor(R.color.calendarTimeBackground),
            colorTitle,
            colorCurrentTitle
        )
        recyclerAdapter.dayClick += { date ->
            viewModel.date.value = date
            findNavController().navigateUp()
        }

        recyclerView.itemAnimator = null
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = recyclerAdapter
        //val q = DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
        //val e = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        //q.setDrawable(recyclerView.context.getDrawable(R.drawable.all_divider)!!)
        //e.setDrawable(recyclerView.context.getDrawable(R.drawable.all_divider)!!)
        //recyclerView.addItemDecoration(q)
        //recyclerView.addItemDecoration(e)

        recyclerView.scrollToPosition(
            recyclerAdapter.firstPosDate.until(viewModel.date.value, ChronoUnit.DAYS).toInt()
        )

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
