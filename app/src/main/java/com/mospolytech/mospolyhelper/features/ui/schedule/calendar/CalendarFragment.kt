package com.mospolytech.mospolyhelper.features.ui.schedule.calendar

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleCalendarBinding
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleIntent
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonFeaturesSettings
import com.mospolytech.mospolyhelper.utils.safe
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.temporal.ChronoUnit

class CalendarFragment : DialogFragment(R.layout.fragment_schedule_calendar) {

    private val viewModel by sharedViewModel<ScheduleViewModel>()
    private val viewBinding by viewBinding(FragmentScheduleCalendarBinding::bind)

    override fun getTheme() = R.style.CustomDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.toolbarScheduleCalendar.toolbar.setNavigationOnClickListener {
            findNavController().safe { navigateUp() }
        }

        val recyclerAdapter = CalendarThreeAdapter(
            viewModel.store.state.filteredSchedule,
            viewModel.selectedScheduleSource.value?.let {
                LessonFeaturesSettings.fromUserSchedule(it)
            } ?: LessonFeaturesSettings(true, true, true)
        )
        recyclerAdapter.dayClick += { date ->
            viewModel.store.onIntent(ScheduleIntent.SetDate(date))
            findNavController().safe { navigateUp() }
        }

        viewBinding.recyclerScheduleDay.itemAnimator = null
        viewBinding.recyclerScheduleDay.layoutManager = GridLayoutManager(context, 3)
        viewBinding.recyclerScheduleDay.adapter = recyclerAdapter

        viewBinding.recyclerScheduleDay.scrollToPosition(
            recyclerAdapter.firstPosDate.until(viewModel.store.state.date, ChronoUnit.DAYS).toInt()
        )

//        viewBinding.toolbarScheduleCalendar.btngroupScheduleCalendar.addOnButtonCheckedListener { group, checkedId, isChecked ->
//            if (!isChecked) {
//                return@addOnButtonCheckedListener
//            }
//            when (checkedId) {
//                R.id.btn_schedule_calendar_three -> {
//                    viewBinding.recyclerScheduleDay.layoutManager = GridLayoutManager(context, 3)
//                    val recyclerAdapter = CalendarThreeAdapter(
//                        viewModel.filteredSchedule.value.getOrNull()
//                    )
//                    viewBinding.recyclerScheduleDay.adapter = recyclerAdapter
//                    recyclerAdapter.dayClick += { date ->
//                        viewModel.date.value = date
//                        findNavController().safe { navigateUp() }
//                    }
//                }
//            }
//        }
    }
}
