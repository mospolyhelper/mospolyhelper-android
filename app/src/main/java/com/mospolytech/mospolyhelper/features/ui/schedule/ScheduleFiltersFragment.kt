package com.mospolytech.mospolyhelper.features.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.BottomSheetScheduleFiltersBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ScheduleFiltersFragment : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<ScheduleViewModel>()
    private val viewBinding by viewBinding(BottomSheetScheduleFiltersBinding::bind)

    override fun getTheme(): Int  = R.style.CustomBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_schedule_filters, container, false)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        lifecycleScope.launchWhenResumed {
//            var currentState: ScheduleState? = null
//            viewModel.store.statesFlow.collect {
//                val state = StatePair(currentState, it)
//                currentState = it
//                renderUi(state)
//            }
//        }
//
//        viewBinding.chipLessonDatesEnded.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.store.state.lessonDateFilter?.let {
//                viewModel.store.sendIntent(
//                    ScheduleIntent.SetLessonDateFilter(it.copy(showEndedLessons = isChecked))
//                )
//            }
//        }
//        viewBinding.chipLessonDatesCurrent.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.store.state.lessonDateFilter?.let {
//                viewModel.store.sendIntent(
//                    ScheduleIntent.SetLessonDateFilter(it.copy(showCurrentLessons = isChecked))
//                )
//            }
//        }
//        viewBinding.chipLessonDatesNotStarted.setOnCheckedChangeListener { _, isChecked ->
//            viewModel.store.state.lessonDateFilter?.let {
//                viewModel.store.sendIntent(
//                    ScheduleIntent.SetLessonDateFilter(it.copy(showNotStartedLessons = isChecked))
//                )
//            }
//        }
//    }
//
//    private fun renderUi(state: StatePair<ScheduleState>) {
//        state.onChanged({ allLessonTypes }) {
//            for (type in it.allLessonTypes) {
//                viewBinding.chipgroupLessonTypes.addView(createFilterChip(type))
//            }
//        }
//
//        state.onChanged({ lessonDateFilter }) {
//            viewBinding.chipLessonDatesEnded.isChecked =
//                it.lessonDateFilter?.showEndedLessons ?: false
//
//            viewBinding.chipLessonDatesCurrent.isChecked =
//                it.lessonDateFilter?.showCurrentLessons ?: false
//
//            viewBinding.chipLessonDatesCurrent.isChecked =
//                it.lessonDateFilter?.showNotStartedLessons ?: false
//        }
//    }
//
//    private fun createFilterChip(filter: String): Chip {
//        val chip = layoutInflater.inflate(R.layout.chip_schedule_filter, viewBinding.chipgroupLessonTypes, false) as Chip
//        chip.text = filter
//        chip.isChecked = filter in viewModel.filterTypes.value
//        chip.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                viewModel.addTypeFilter(filter)
//            } else {
//                viewModel.removeTypeFilter(filter)
//            }
//        }
//        return chip
//    }
}