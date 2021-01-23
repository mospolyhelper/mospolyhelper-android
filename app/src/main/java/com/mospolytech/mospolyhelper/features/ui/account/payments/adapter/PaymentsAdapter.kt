package com.mospolytech.mospolyhelper.features.ui.account.payments.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payment
import com.mospolytech.mospolyhelper.utils.inflate

class PaymentsAdapter(private val items: List<Payment>): RecyclerView.Adapter<PaymentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentsViewHolder {
        return PaymentsViewHolder(parent.inflate(R.layout.item_payment))
    }

    override fun onBindViewHolder(holder: PaymentsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }
}