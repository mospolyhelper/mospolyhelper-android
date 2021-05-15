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
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountClassmatesBinding
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.features.ui.account.classmates.adapter.ClassmatesAdapter
import com.mospolytech.mospolyhelper.features.ui.account.marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.features.ui.account.messaging.MessagingFragment.Companion.DIALOG_ID
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_classmates.*
import kotlinx.android.synthetic.main.fragment_account_classmates.progress_loading
import kotlinx.android.synthetic.main.fragment_account_marks.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClassmatesFragment : Fragment(R.layout.fragment_account_classmates) {

    private val viewBinding by viewBinding(FragmentAccountClassmatesBinding::bind)
    private val viewModel by viewModel<ClassmatesViewModel>()

    private val adapter = ClassmatesAdapter()
    private var classmates: List<Classmate> = emptyList()

    private val editor = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            val classmatesMutable: MutableList<Classmate> = classmates.toMutableList()
            adapter.items = classmatesMutable.filter { predicate ->
                predicate.name.contains(p0.toString(), true)
            }
        }
        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { }
        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ClassmatesAdapter.classmatesClickListener = {
            val data = bundleOf(DIALOG_ID to it)
            findNavController().navigate(R.id.action_classmatesFragment_to_messagingFragment, data)
        }

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.recyclerClassmates.adapter = adapter

        viewBinding.swipeClassmates.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo()
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.classmates.collect { result ->
                result.onSuccess {
                    viewBinding.swipeClassmates.isRefreshing = false
                    viewBinding.progressLoading.gone()
                    classmates = it
                    if (viewBinding.editSearchClassmate.text.isNotEmpty()) {
                        val classmatesMutable: MutableList<Classmate> = classmates.toMutableList()
                        adapter.items = classmatesMutable.filter { predicate ->
                            predicate.name.contains(viewBinding.editSearchClassmate.text.toString(), true)
                        }
                    } else {
                        adapter.items = classmates
                    }
                }.onFailure {
                    viewBinding.swipeClassmates.isRefreshing = false
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    viewBinding.progressLoading.gone()
                }.onLoading {
                    if (!viewBinding.swipeClassmates.isRefreshing)
                        viewBinding.progressLoading.show()
                }
            }
        }

    }

    override fun onResume() {
        viewBinding.editSearchClassmate.addTextChangedListener(editor)
        super.onResume()
    }

    override fun onPause() {
        viewBinding.editSearchClassmate.removeTextChangedListener(editor)
        super.onPause()
    }

    override fun onDestroy() {
        ClassmatesAdapter.classmatesClickListener = null
        super.onDestroy()
    }
}