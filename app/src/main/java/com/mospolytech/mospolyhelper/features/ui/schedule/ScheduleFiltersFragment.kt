package com.mospolytech.mospolyhelper.features.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.utils.getAllTypes
import com.mospolytech.mospolyhelper.utils.onSuccess
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ScheduleFiltersFragment: BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<ScheduleViewModel>()

    private lateinit var lessonTypesChipGroup: ChipGroup
    private lateinit var lessonDatesChipGroup: ChipGroup
    private lateinit var lessonLabelsChipGroup: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_schedule_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lessonTypesChipGroup = view.findViewById(R.id.chipgroup_lesson_types)
        lessonDatesChipGroup = view.findViewById(R.id.chipgroup_lesson_dates)
        lessonLabelsChipGroup = view.findViewById(R.id.chipgroup_lesson_labels)

        viewModel.originalSchedule.value.onSuccess {
            if (it.schedule == null) {
                return@onSuccess
            }
            for (type in it.schedule.getAllTypes()) {
                lessonTypesChipGroup.addView(createFilterChip(type))
            }
        }
        val chipDateEnded = lessonDatesChipGroup.findViewById<Chip>(R.id.chip_lesson_dates_ended)
        val chipDateCurrent = lessonDatesChipGroup.findViewById<Chip>(R.id.chip_lesson_dates_current)
        val chipDateNotStarted = lessonDatesChipGroup.findViewById<Chip>(R.id.chip_lesson_dates_not_started)

        val chipLabelsImportant = lessonLabelsChipGroup.findViewById<Chip>(R.id.chip_lesson_labels_important)
        val chipLabelsAverage = lessonLabelsChipGroup.findViewById<Chip>(R.id.chip_lesson_labels_average)
        val chipLabelsNotImportant = lessonLabelsChipGroup.findViewById<Chip>(R.id.chip_lesson_labels_not_important)
        val chipLabelsNotLabeled = lessonLabelsChipGroup.findViewById<Chip>(R.id.chip_lesson_labels_not_labeled)

        if (viewModel.showEndedLessons.value) {
            chipDateEnded.isChecked = true
        }
        if (viewModel.showCurrentLessons.value) {
            chipDateCurrent.isChecked = true
        }
        if (viewModel.showNotStartedLessons.value) {
            chipDateNotStarted.isChecked = true
        }

        if (viewModel.showImportantLessons.value) {
            chipLabelsImportant.isChecked = true
        }
        if (viewModel.showAverageLessons.value) {
            chipLabelsAverage.isChecked = true
        }
        if (viewModel.showNotImportantLessons.value) {
            chipLabelsNotImportant.isChecked = true
        }
        if (viewModel.showNotLabeledLessons.value) {
            chipLabelsNotLabeled.isChecked = true
        }



        chipDateEnded.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showEndedLessons.value = isChecked
        }
        chipDateCurrent.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showCurrentLessons.value = isChecked
        }
        chipDateNotStarted.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showNotStartedLessons.value = isChecked
        }


        chipLabelsImportant.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showImportantLessons.value = isChecked
        }
        chipLabelsAverage.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showAverageLessons.value = isChecked
        }
        chipLabelsNotImportant.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showNotImportantLessons.value = isChecked
        }
        chipLabelsNotLabeled.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.showNotLabeledLessons.value = isChecked
        }
    }

    private fun createFilterChip(filter: String): Chip {
        val chip = layoutInflater.inflate(R.layout.chip_schedule_filter, lessonTypesChipGroup, false) as Chip
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