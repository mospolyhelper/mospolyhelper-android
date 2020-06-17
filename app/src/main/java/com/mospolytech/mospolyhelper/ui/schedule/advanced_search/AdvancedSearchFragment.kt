package com.mospolytech.mospolyhelper.ui.schedule.advanced_search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R


class AdvancedSearchFragment : DialogFragment() {

    companion object {
        fun newInstance() = AdvancedSearchFragment()
    }

    private var adapter: AdvancedSearchAdapter? = null
    var checkedAll: Boolean = false
    var selectAll: String = ""
    var unselectAll: String = ""

    private val viewModel by viewModels<AdvancedSearchViewModel>()

    fun setAdapter(adapter: AdvancedSearchAdapter) {
        this.adapter = adapter
        if (dialog == null) return
        val recyclerView = requireDialog().findViewById<RecyclerView>(R.id.recycler_advanced_search)
        if (recyclerView != null) {
            val searchView = requireDialog().findViewById<SearchView>(R.id.searchView1)
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.updateTemplate(newText ?: "")
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
            })
            recyclerView.adapter = adapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_filter, container, false)

        selectAll = getString(R.string.select_all)
        unselectAll = getString(R.string.unselect_all)
        dialog?.window?.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
        val checkAll = view.findViewById<Button>(R.id.button_check_all)
        checkAll.text = if (checkedAll) unselectAll else selectAll
        checkAll.setOnClickListener {
            checkedAll = !checkedAll
            adapter?.setCheckAll(checkedAll)
            checkAll.text = if (checkedAll) unselectAll else selectAll
        }
        val adapter = this.adapter
        if (adapter != null) {
            adapter.allCheckedChanged += {
                checkedAll = it
                checkAll.text = if (checkedAll) unselectAll else selectAll
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val recyclerView = requireDialog().findViewById<RecyclerView>(R.id.recycler_advanced_search)!!
        val layoutManager = LinearLayoutManager(recyclerView.context)
        val divider = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        divider.setDrawable(requireContext().getDrawable(R.drawable.all_divider)!!)
        recyclerView.addItemDecoration(divider)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.adapter?.notifyDataSetChanged()

        requireDialog().findViewById<SearchView>(R.id.searchView1)?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.updateTemplate(newText ?: "")
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}
