package com.mospolytech.mospolyhelper.features.ui.account.payments.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payment
import kotlinx.android.synthetic.main.item_payment.view.*

class PaymentsViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val date: TextView = view.date_payment
    val cost: TextView = view.cost_payment

    fun bind(payment: Payment) {
        date.text = payment.date
        cost.text = "${payment.amount} руб."
    }
}