package com.mospolytech.mospolyhelper.features.ui.schedule.calendar

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleCalendarBinding
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleIntent
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search.AdvancedSearchFragment
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonFeaturesSettings
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalendarFragment : DialogFragment(R.layout.fragment_schedule_calendar) {
    companion object {
        const val CALENDAR_FRAGMENT = "calendar_fragment"
    }

    private val viewModel by viewModel<CalendarViewModel>()
    private val viewBinding by viewBinding(FragmentScheduleCalendarBinding::bind)

    override fun getTheme() = R.style.CustomDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.toolbarScheduleCalendar.toolbar.setNavigationOnClickListener {
            findNavController().safe { navigateUp() }
        }

        viewBinding.recyclerScheduleDay.itemAnimator = null
        viewBinding.recyclerScheduleDay.layoutManager = GridLayoutManager(context, 3)

        lifecycleScope.launchWhenResumed {
            viewModel.schedule.debounce(500L).collect {
                val recyclerAdapter = CalendarThreeAdapter(
                    it,
                    viewModel.scheduleSource.value?.let {
                        LessonFeaturesSettings.fromUserSchedule(it)
                    } ?: LessonFeaturesSettings(true, true, true)
                )
                recyclerAdapter.dayClick += { date ->
                    findNavController().safe {
                        previousBackStackEntry?.savedStateHandle
                            ?.set(CalendarFragment.CALENDAR_FRAGMENT, date.toEpochDay())
                        navigateUp()
                    }
                }

                viewBinding.recyclerScheduleDay.adapter = recyclerAdapter
                viewBinding.recyclerScheduleDay.scrollToPosition(
                    recyclerAdapter.firstPosDate.until(LocalDate.now(), ChronoUnit.DAYS).toInt()
                )
            }
        }





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
