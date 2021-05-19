package com.mospolytech.mospolyhelper.features.ui.account.payments.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemPaymentBinding
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payment
import com.mospolytech.mospolyhelper.utils.inflate

class PaymentsAdapter(private val items: List<Payment>): RecyclerView.Adapter<PaymentsAdapter.PaymentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentsViewHolder {
        return PaymentsViewHolder(parent.inflate(R.layout.item_payment))
    }

    override fun onBindViewHolder(holder: PaymentsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    class PaymentsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemPaymentBinding::bind)

        private val date: TextView = viewBinding.datePayment
        private val cost: TextView = viewBinding.costPayment

        fun bind(payment: Payment) {
            date.text = payment.date
            cost.text = itemView.context.getString(R.string.payment, payment.amount.toString())
        }
    }
}