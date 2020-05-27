package com.mospolytech.mospolyhelper.ui.schedule.calendar

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.MainActivity

import com.mospolytech.mospolyhelper.R
import java.util.concurrent.TimeUnit

class CalendarFragment : Fragment() {

    companion object {
        fun newInstance() = CalendarFragment()
    }

    var dateChanged = false

    private val viewModel by viewModels<CalendarViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_calendar, container, false)

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
        // TODO Fix schedule null
        val recyclerAdapter = CalendarAdapter(viewModel.schedule!!, viewModel.scheduleFilter,
        viewModel.isAdvancedSearch, requireContext().getColor(R.color.calendarParagraph),
            requireContext().getColor(R.color.calendarTimeBackground), colorTitle, colorCurrentTitle);
        recyclerAdapter.addOnDayClick {date ->
            viewModel.date = date
            dateChanged = true
            activity?.onBackPressed()
        }

        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN &&
                    rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING
                ) {
                    rv.stopScroll();
                    return true
                }
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
        })
        recyclerView.itemAnimator = null
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = recyclerAdapter
        val q = DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
        val e = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        q.setDrawable(recyclerView.context.getDrawable(R.drawable.all_divider)!!)
        e.setDrawable(recyclerView.context.getDrawable(R.drawable.all_divider)!!)
        recyclerView.addItemDecoration(q)
        recyclerView.addItemDecoration(e)

        recyclerView.scrollToPosition(TimeUnit.DAYS
            .convert(
                viewModel.date.time.time - recyclerAdapter.firstPosDate.time.time,
                TimeUnit.MILLISECONDS
            ).toInt())

        return view
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
