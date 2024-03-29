package com.mospolytech.mospolyhelper.features.ui.account.payments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountPaymentsBinding
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.features.ui.account.payments.adapter.PagerAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException

class PaymentsFragment: Fragment(R.layout.fragment_account_payments) {

    private val viewBinding by viewBinding(FragmentAccountPaymentsBinding::bind)
    private val viewModel by viewModel<PaymentsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.paymentsSwipe.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo()
            }
        }

        viewBinding.paymentPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                viewBinding.paymentsSwipe.isEnabled = state == ViewPager2.SCROLL_STATE_IDLE
                super.onPageScrollStateChanged(state)
            }
        })

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
                        viewBinding.paymentsSwipe.isRefreshing = false
                        Toast.makeText(context, result.exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                    is Result0.Loading -> {
                        if (!viewBinding.paymentsSwipe.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.payments.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.paymentsSwipe.show()
                        fillData(result.value)
                        viewBinding.paymentsSwipe.isRefreshing = false
                    }
                    is Result0.Failure -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.paymentsSwipe.isRefreshing = false
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
                        if (!viewBinding.paymentsSwipe.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }
    }
    private fun fillData(payments: Payments) {
        viewBinding.paymentPager.adapter = PagerAdapter(payments.contracts.values.toList())
        val titles = payments.contracts.keys.toTypedArray()
        TabLayoutMediator(viewBinding.paymentTabs, viewBinding.paymentPager) { tab, position ->
            tab.text =
                when {
                    titles[position] == "dormitory" -> requireContext().getString(R.string.dormitory)
                    titles[position] == "tuition" -> requireContext().getString(R.string.tuition)
                    else -> titles[position]
                }
        }.attach()
    }
}