package com.mospolytech.mospolyhelper.features.ui.schedule.ids

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScheduleIdsFragment: DialogFragment() {

    private val viewModel by viewModel<ScheduleIdsViewModel>()

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var editText: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_ids, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar_schedule_id)
        editText = view.findViewById(R.id.edittext_schedule_id)
        chipGroup = view.findViewById(R.id.chipgroup_schedule_id_type)
        recyclerView = view.findViewById(R.id.recyclerview_schedule_ids)
        progressBar = view.findViewById(R.id.progressBar)

        setToolbar()
        setEditText()
        setChipGroup()
        setRecyclerView()
        bindViewModel()
    }

    private fun setToolbar() {
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setEditText() {
        editText.doAfterTextChanged {
            if (it != null) {
                viewModel.searchQuery.value = it.toString()
            } else {
                viewModel.searchQuery.value = ""
            }
        }
    }

    private fun setChipGroup() {
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
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
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private var progressBarFlag = true

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.idSet.collect {
                setAdapter(it.toList())
                if (progressBarFlag) {
                    progressBarFlag = false
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            combine(viewModel.searchQuery, viewModel.filterMode) { query, filterMode ->
                (recyclerView.adapter as? ScheduleIdsAdapter)?.update(filterMode, query)
            }.collect()
        }
    }

    private fun setAdapter(idList: List<Pair<Boolean, String>>) {
        recyclerView.adapter = ScheduleIdsAdapter(
            idList,
            viewModel.searchQuery.value,
            viewModel.filterMode.value
        ) {
            viewModel.sendSelectedItem(it)
            findNavController().safe { navigateUp() }
        }
    }
}