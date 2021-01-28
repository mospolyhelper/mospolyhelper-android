package com.mospolytech.mospolyhelper.features.ui.account.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.features.ui.account.info.adapter.OrderAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.android.synthetic.main.fragment_account_info.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class InfoFragment : Fragment() {

    private lateinit var authSnackbar: Snackbar

    private val viewModel by viewModel<InfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        info_swipe.setOnRefreshListener {
            lifecycleScope.async {
                viewModel.downloadInfo()
            }
        }

        authSnackbar = Snackbar.make(info_swipe,
            R.string.unauthorized, Snackbar.LENGTH_SHORT)
        authSnackbar.setAction(R.string.authorize) {
            findNavController().navigate(R.id.action_infoFragment_to_authFragment)
        }

        lifecycleScope.launchWhenResumed {
            viewModel.info.collect { result ->
                result.onSuccess {
                    progress_loading.gone()
                    info_layout.show()
                    filldata(it)
                    info_swipe.isRefreshing = false
                }.onFailure {
                    progress_loading.gone()
                    info_swipe.isRefreshing = false
                    if (it is ClientRequestException) {
                        if (it.response?.status?.value == 401) {
                            authSnackbar.show()
                        }
                    } else
                        Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                }.onLoading {
                    if (!info_swipe.isRefreshing)
                        progress_loading.show()
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
        }

    }
    fun filldata(info: Info) {
        val information = String.format(resources.getString(R.string.base_info), info.educationLevel,
            info.course, info.group)
        info_student.text = information
        facult_student.text = info.faculty
        direction_student.text = info.direction
        spec_student.text = info.specialization
        fio_student.text = info.name
        payment_student.text = "${info.financingType} ${info.educationForm.toLowerCase()} форма обучения"
        year_student.text = "${info.admissionYear} год набора"
        orders.adapter = OrderAdapter(info.orders)
        //infoText.text = information
    }
}