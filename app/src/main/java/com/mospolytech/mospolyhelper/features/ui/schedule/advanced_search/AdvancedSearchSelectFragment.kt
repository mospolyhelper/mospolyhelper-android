package com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleFilterBinding

class AdvancedSearchSelectFragment : DialogFragment() {

    companion object {
        fun newInstance() = AdvancedSearchSelectFragment()
    }

    private var adapter: AdvancedSearchAdapter? = null
    private var checkedAll: Boolean = false
    private var selectAll: String = ""
    private var unselectAll: String = ""

    private val viewModel by viewModels<AdvancedSearchSelectViewModel>()
    private val viewBinding by viewBinding(FragmentScheduleFilterBinding::bind)

    fun setAdapter(adapter: AdvancedSearchAdapter) {
        this.adapter = adapter
        if (dialog == null) return
        viewBinding.searchView1.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.updateTemplate(newText ?: "")
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })
        viewBinding.recyclerAdvancedSearch.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.all_divider)!!)
        viewBinding.recyclerAdvancedSearch.addItemDecoration(divider)
        viewBinding.recyclerAdvancedSearch.layoutManager = layoutManager
        viewBinding.recyclerAdvancedSearch.adapter = adapter
        viewBinding.recyclerAdvancedSearch.adapter?.notifyDataSetChanged()

        viewBinding.searchView1.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.updateTemplate(newText ?: "")
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })


        selectAll = getString(R.string.select_all)
        unselectAll = getString(R.string.unselect_all)
        viewBinding.buttonCheckAll.text = if (checkedAll) unselectAll else selectAll
        viewBinding.buttonCheckAll.setOnClickListener {
            checkedAll = !checkedAll
            adapter?.setCheckAll(checkedAll)
            viewBinding.buttonCheckAll.text = if (checkedAll) unselectAll else selectAll
        }
        this.adapter?.let {
            it.allCheckedChanged += {
                checkedAll = it
                viewBinding.buttonCheckAll.text = if (checkedAll) unselectAll else selectAll
            }
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
    }
}
