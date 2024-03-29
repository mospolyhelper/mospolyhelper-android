package com.mospolytech.mospolyhelper.features.ui.account.info

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountInfoBinding
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.features.ui.account.info.adapter.OrderAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException
import java.util.*

class InfoFragment : Fragment(R.layout.fragment_account_info) {

    private val viewBinding by viewBinding(FragmentAccountInfoBinding::bind)
    private val viewModel by viewModel<InfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.infoSwipe.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo()
            }
        }

        Glide.with(this).load(viewModel.getAvatar()).circleCrop().into(viewBinding.avatarStudent)

        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        lifecycleScope.launch {
                            viewModel.downloadInfo()
                        }
                    }
                    is Result0.Failure -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.infoSwipe.isRefreshing = false
                        Toast.makeText(context, result.exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                    is Result0.Loading -> {
                        if (!viewBinding.infoSwipe.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.info.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.infoLayout.show()
                        fillData(result.value)
                        viewBinding.infoSwipe.isRefreshing = false
                    }
                    is Result0.Failure -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.infoSwipe.isRefreshing = false
                        when (val error = result.exception) {
                            is ClientRequestException -> {
                                when (error.response.status.value) {
                                    401 ->  {
                                        lifecycleScope.launch {
                                            viewModel.refresh()
                                        }
                                    }
                                    else -> Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show()
                                }
                            }
                            is UnknownHostException -> {
                                Toast.makeText(context, R.string.check_connection, Toast.LENGTH_LONG).show()
                            }
                            else -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    is Result0.Loading -> {
                        if (!viewBinding.infoSwipe.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }

    }
    
    private fun fillData(info: Info) {
        val information = String.format(resources.getString(R.string.base_info), info.educationLevel,
            info.course, info.group)
        viewBinding.infoStudent.text = information
        viewBinding.facultStudent.text = info.faculty
        viewBinding.directionStudent.text = info.direction
        viewBinding.specStudent.text = info.specialization
        viewBinding.fioStudent.text = info.name
        viewBinding.paymentStudent.text = String.format(resources.getString(R.string.ed_form), info.financingType,
            info.educationForm.toLowerCase(Locale.ROOT))
        viewBinding.yearStudent.text = String.format(resources.getString(R.string.ed_year), info.admissionYear)
        if (info.dormitory.isNotEmpty()) {
            viewBinding.apartmentStudent.text =
                requireContext().getString(R.string.apartment, info.dormitory, info.dormitoryRoom)
            viewBinding.apartmentStudent.show()
        }
        viewBinding.orders.adapter = OrderAdapter(info.orders)
    }

    override fun onDestroyView() {
        Glide.with(this).clear(viewBinding.avatarStudent)
        super.onDestroyView()
    }
}