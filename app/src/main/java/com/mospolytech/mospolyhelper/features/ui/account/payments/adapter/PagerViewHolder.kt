package com.mospolytech.mospolyhelper.features.ui.account.payments.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.payments.model.Contract
import kotlinx.android.synthetic.main.item_payments.view.*

class PagerViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val recycler: RecyclerView = view.recycler_dormitory
    val info: TextView = view.payment_info
    val all: TextView = view.payment_all
    val current: TextView = view.payment_current

    fun bind(contract: Contract) {
        recycler.adapter = PaymentsAdapter(contract.payments)
        recycler.layoutManager = LinearLayoutManager(itemView.context)
        info.text = contract.name
        all.text = String.format(itemView.context.getString(R.string.payment_left),
            contract.remainingAmount, contract.paidAmount)
        current.text = when {
            contract.debt < 0 -> {
                String.format(itemView.context.getString(R.string.payment_debet),
                    contract.debt, contract.debtDate).replace("-", "")
            }
            contract.debt == 0 -> {
                String.format(itemView.context.getString(R.string.payment_no_credit),
                    contract.debtDate)
            }
            else -> {
                String.format(itemView.context.getString(R.string.payment_credit),
                    contract.debt, contract.debtDate)
            }
        }
    }
}