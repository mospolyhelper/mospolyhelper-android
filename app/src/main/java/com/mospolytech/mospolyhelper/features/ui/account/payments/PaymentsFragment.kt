package com.mospolytech.mospolyhelper.features.ui.account.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.features.ui.account.payments.adapter.PagerAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_payments.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentsFragment: Fragment() {


    private val viewModel by viewModel<PaymentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        payments_swipe.setOnRefreshListener {
            lifecycleScope.async {
                viewModel.downloadInfo()
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.payments.collect { result ->
                result.onSuccess {
                    progress_loading.gone()
                    payments_swipe.show()
                    filldata(it)
                    payments_swipe.isRefreshing = false
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    progress_loading.gone()
                    payments_swipe.isRefreshing = false
                }.onLoading {
                    if (!payments_swipe.isRefreshing)
                        progress_loading.show()
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
        }


    }
    fun filldata(payments: Payments) {
        payment_pager.adapter = PagerAdapter(payments.contracts.values.toList())
        val titles = payments.contracts.keys.toTypedArray()
        TabLayoutMediator(payment_tabs, payment_pager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}