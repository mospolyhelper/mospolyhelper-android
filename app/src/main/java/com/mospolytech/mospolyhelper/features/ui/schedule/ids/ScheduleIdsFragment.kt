package com.mospolytech.mospolyhelper.features.ui.schedule.ids

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleIdsBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScheduleIdsFragment: DialogFragment(R.layout.fragment_schedule_ids) {

    private val viewModel by viewModel<ScheduleIdsViewModel>()
    private val viewBinding by viewBinding(FragmentScheduleIdsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        setEditText()
        setChipGroup()
        setRecyclerView()
        bindViewModel()
    }

    private fun setToolbar() {
        (activity as MainActivity).setSupportActionBar(viewBinding.toolbarScheduleId)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setEditText() {
        viewBinding.edittextScheduleId.doAfterTextChanged {
            if (it != null) {
                viewModel.searchQuery.value = it.toString()
            } else {
                viewModel.searchQuery.value = ""
            }
        }
    }

    private fun setChipGroup() {
        viewBinding.chipgroupScheduleIdType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_schedule_id_all ->
                    viewModel.filterMode.value = FilterModes.All
                R.id.chip_schedule_id_groups ->
                    viewModel.filterMode.value = FilterModes.Groups
                R.id.chip_schedule_id_teachers ->
                    viewModel.filterMode.value = FilterModes.Teachers
            }
        }
    }

    private fun setRecyclerView() {
        viewBinding.recyclerviewScheduleIds.layoutManager = LinearLayoutManager(context)
    }

    private var progressBarFlag = true

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.idSet.collect {
                setAdapter(it.toList())
                if (progressBarFlag) {
                    progressBarFlag = false
                } else {
                    viewBinding.progressBar.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            combine(viewModel.searchQuery, viewModel.filterMode) { query, filterMode ->
                (viewBinding.recyclerviewScheduleIds.adapter as? ScheduleIdsAdapter)?.update(filterMode, query)
            }.collect()
        }
    }

    private fun setAdapter(idList: List<UserSchedule>) {
        viewBinding.recyclerviewScheduleIds.adapter = ScheduleIdsAdapter(
            idList,
            viewModel.searchQuery.value,
            viewModel.filterMode.value
        ) {
            viewModel.sendSelectedItem(it)
            findNavController().safe { navigateUp() }
        }
    }
}