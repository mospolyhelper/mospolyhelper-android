package com.mospolytech.mospolyhelper.features.ui.account.classmates

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.features.ui.account.classmates.adapter.ClassmatesAdapter
import com.mospolytech.mospolyhelper.features.ui.account.marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_classmates.*
import kotlinx.android.synthetic.main.fragment_account_classmates.progress_loading
import kotlinx.android.synthetic.main.fragment_account_marks.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClassmatesFragment : Fragment() {

    private val viewModel by viewModel<ClassmatesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_classmates, container, false)
    }

    var classmates: List<Classmate> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_classmates.layoutManager = LinearLayoutManager(requireContext())
        recycler_classmates.adapter = ClassmatesAdapter(classmates) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            val data = bundleOf("DialogID" to it)
            findNavController().navigate(R.id.action_classmatesFragment_to_messagingFragment, data)
        }
        val editor = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                recycler_classmates.adapter?.let { adapter ->
                    if (adapter is ClassmatesAdapter) {
                        val classmatesMutable: MutableList<Classmate> = classmates.toMutableList()
                        adapter.updateList(classmatesMutable.filter { predicate ->
                            predicate.name.contains(p0.toString(), true)
                        })
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { } }

        lifecycleScope.launchWhenResumed {
            viewModel.classmates.collect { result ->
                result.onSuccess {
                    progress_loading.gone()
                    classmates = it
                    recycler_classmates.adapter?.let { adapter ->
                        if (adapter is ClassmatesAdapter) adapter.updateList(classmates)
                    }
                    edit_search_classmate.addTextChangedListener(editor)
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    progress_loading.gone()
                    if (recycler_classmates.adapter?.itemCount == 0) {
                        edit_search_classmate.removeTextChangedListener(editor)
                    }
                }.onLoading {
                    progress_loading.show()
                    if (recycler_classmates.adapter?.itemCount == 0) {
                        edit_search_classmate.removeTextChangedListener(editor)
                    }
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
            }

    }
}