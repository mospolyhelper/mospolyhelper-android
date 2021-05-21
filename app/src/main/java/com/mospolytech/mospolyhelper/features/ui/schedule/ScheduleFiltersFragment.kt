package com.mospolytech.mospolyhelper.features.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.BottomSheetScheduleFiltersBinding
import com.mospolytech.mospolyhelper.domain.schedule.utils.getAllTypes
import com.mospolytech.mospolyhelper.utils.onSuccess
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
        
        viewModel.originalSchedule.value.onSuccess {
            if (it.schedule == null) {
                return@onSuccess
            }
            for (type in it.schedule.getAllTypes()) {
                viewBinding.chipgroupLessonTypes.addView(createFilterChip(type))
            }
        }

        if (viewModel.showEndedLessons.value) {
            viewBinding.chipLessonDatesEnded.isChecked = true
        }
        if (viewModel.showCurrentLessons.value) {
            viewBinding.chipLessonDatesCurrent.isChecked = true
        }
        if (viewModel.showNotStartedLessons.value) {
            viewBinding.chipLessonDatesNotStarted.isChecked = true
        }

        if (viewModel.showImportantLessons.value) {
            viewBinding.chipLessonLabelsImportant.isChecked = true
        }
        if (viewModel.showAverageLessons.value) {
            viewBinding.chipLessonLabelsAverage.isChecked = true
        }
        if (viewModel.showNotImportantLessons.value) {
            viewBinding.chipLessonLabelsNotImportant.isChecked = true
        }



        viewBinding.chipLessonDatesEnded.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showEndedLessons.value = isChecked
        }
        viewBinding.chipLessonDatesCurrent.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showCurrentLessons.value = isChecked
        }
        viewBinding.chipLessonDatesNotStarted.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showNotStartedLessons.value = isChecked
        }


        viewBinding.chipLessonLabelsImportant.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showImportantLessons.value = isChecked
        }
        viewBinding.chipLessonLabelsAverage.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showAverageLessons.value = isChecked
        }
        viewBinding.chipLessonLabelsNotImportant.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showNotImportantLessons.value = isChecked
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