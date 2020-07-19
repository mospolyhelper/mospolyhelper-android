package com.mospolytech.mospolyhelper.ui.schedule.calendar

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.MainActivity

import com.mospolytech.mospolyhelper.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.temporal.ChronoUnit

class CalendarFragment : DialogFragment() {

    companion object {
        fun newInstance() = CalendarFragment()
    }

    var dateChanged = false

    private val viewModel by viewModel<CalendarViewModel>()

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

        var groupTitle = viewModel.schedule?.group?.title ?: ""
        groupTitle = if (groupTitle.isEmpty()) {
            getString(R.string.advanced_search)
        } else {
            "$groupTitle (" + (
                    if (viewModel.schedule?.isSession == true)
                        getString(R.string.text_schedule_type_session_s)
                    else
                        getString(R.string.text_schedule_type_regular_s)) + ")"
        }
        toolbar.title = groupTitle

        (activity as MainActivity).setSupportActionBar(toolbar);
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_schedule_day)
        val colorTitle = requireContext().getColor(R.color.calendarTitle)
        val colorCurrentTitle = requireContext().getColor(R.color.calendarCurrentTitle)
        val recyclerAdapter = CalendarAdapter(viewModel.schedule!!, viewModel.scheduleFilter,
        viewModel.isAdvancedSearch, requireContext().getColor(R.color.calendarParagraph),
            requireContext().getColor(R.color.calendarTimeBackground), colorTitle, colorCurrentTitle);
        recyclerAdapter.dayClick += { date ->
            viewModel.date = date
            dateChanged = true
            findNavController().navigateUp()
        }

        recyclerView.itemAnimator = null
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = recyclerAdapter
        val q = DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
        val e = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        q.setDrawable(recyclerView.context.getDrawable(R.drawable.all_divider)!!)
        e.setDrawable(recyclerView.context.getDrawable(R.drawable.all_divider)!!)
        recyclerView.addItemDecoration(q)
        recyclerView.addItemDecoration(e)

        recyclerView.scrollToPosition(
            recyclerAdapter.firstPosDate.until(viewModel.date, ChronoUnit.DAYS).toInt()
        )

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onStop() {
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        if (dateChanged) {
            viewModel.dateChanged()
        }
        super.onStop()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}
