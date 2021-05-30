package com.mospolytech.mospolyhelper.features.ui.account.payments.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemPaymentsBinding
import com.mospolytech.mospolyhelper.domain.account.payments.model.Contract

class PagerAdapter(private val items: List<Contract>): RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payments, parent, false))
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PagerViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemPaymentsBinding::bind)

        private val recycler: RecyclerView = viewBinding.recyclerDormitory
        private val info: TextView = viewBinding.paymentInfo
        private val all: TextView = viewBinding.paymentAll
        private val current: TextView = viewBinding.paymentCurrent
        private val sberQr: ImageView = viewBinding.imageSber

        fun bind(contract: Contract) {
            recycler.adapter = PaymentsAdapter(contract.payments)
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
            val url = "https://e.mospolytech.ru/qr.php?data=${contract.sberQR}"
            Glide.with(itemView.context).load(url).into(sberQr)
            sberQr.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add(itemView.context.getString(R.string.open_browser)).setOnMenuItemClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    ContextCompat.startActivity(itemView.context, browserIntent, null)
                    true
                }
            }
        }
    }

}