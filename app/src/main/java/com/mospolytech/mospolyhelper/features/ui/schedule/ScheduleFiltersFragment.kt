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
import com.mospolytech.mospolyhelper.domain.schedule.utils.getAllTypes
import com.mospolytech.mospolyhelper.utils.onReady
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ScheduleFiltersFragment: BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<ScheduleViewModel>()
    private val viewBinding by viewBinding(BottomSheetScheduleFiltersBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_schedule_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.allLessonTypes.collect {
                for (type in it) {
                    viewBinding.chipgroupLessonTypes.addView(createFilterChip(type))
                }
            }
        }

        if (viewModel.lessonDateFilter.value.showEndedLessons) {
            viewBinding.chipLessonDatesEnded.isChecked = true
        }
        if (viewModel.lessonDateFilter.value.showCurrentLessons) {
            viewBinding.chipLessonDatesCurrent.isChecked = true
        }
        if (viewModel.lessonDateFilter.value.showNotStartedLessons) {
            viewBinding.chipLessonDatesNotStarted.isChecked = true
        }



        viewBinding.chipLessonDatesEnded.setOnCheckedChangeListener { _, isChecked ->
            viewModel.lessonDateFilter.value =
                viewModel.lessonDateFilter.value
                    .copy(showEndedLessons = isChecked)
        }
        viewBinding.chipLessonDatesCurrent.setOnCheckedChangeListener { _, isChecked ->
            viewModel.lessonDateFilter.value =
                viewModel.lessonDateFilter.value
                    .copy(showCurrentLessons = isChecked)
        }
        viewBinding.chipLessonDatesNotStarted.setOnCheckedChangeListener { _, isChecked ->
            viewModel.lessonDateFilter.value =
                viewModel.lessonDateFilter.value
                    .copy(showNotStartedLessons = isChecked)
        }
    }

    private fun createFilterChip(filter: String): Chip {
        val chip = layoutInflater.inflate(R.layout.chip_schedule_filter, viewBinding.chipgroupLessonTypes, false) as Chip
        chip.text = filter
        chip.isChecked = filter in viewModel.filterTypes.value
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.filterTypes.value += filter
            } else {
                viewModel.filterTypes.value -= filter
            }
        }
        return chip
    }
}