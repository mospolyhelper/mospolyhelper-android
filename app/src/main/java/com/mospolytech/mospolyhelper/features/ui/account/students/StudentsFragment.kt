package com.mospolytech.mospolyhelper.features.ui.account.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.StudentsAdapter
import com.mospolytech.mospolyhelper.utils.onFailure
import com.mospolytech.mospolyhelper.utils.onLoading
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.android.synthetic.main.fragment_account_students.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class StudentsFragment : Fragment() {

    private val viewModel  by viewModel<StudentsViewModel>()

    private lateinit var studentsSearchResult: StudentsSearchResult

    private val diffUtil = object : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Student, newItem: Student) = oldItem == newItem
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_students.layoutManager = LinearLayoutManager(requireContext())
        val adapter = StudentsAdapter(diffUtil)
        //adapter.submitList()
        recycler_students.adapter = adapter
        lifecycleScope.launchWhenResumed {
            viewModel.students.collect { result ->
                result.onSuccess {
                    //studentsSearchResult = it
                    adapter.removeError()
                    adapter.removeLoading()
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    adapter.removeLoading()
                    adapter.addError()
                }.onLoading {
                    adapter.addLoading()
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
        }
        lifecycleScope.launch {
            viewModel.fetchPosts().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

    }
}