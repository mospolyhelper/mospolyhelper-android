package com.mospolytech.mospolyhelper.features.ui.account.payments.adapter

import android.annotation.SuppressLint
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemPaymentsBinding
import com.mospolytech.mospolyhelper.domain.account.payments.model.Contract
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.show

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
        private val sberQr: TextView = viewBinding.sberQr
        private val qrHelp: TextView = viewBinding.qrHelp
        private val qrContainer: FrameLayout = viewBinding.qrContainer

        @SuppressLint("SetTextI18n")
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

            if (contract.sberQR.isNotEmpty()) {
                sberQr.text = HtmlCompat.fromHtml(
                    "<a href=\"https://e.mospolytech.ru/${contract.sberQR}\">Sber QR</a>",
                    HtmlCompat.FROM_HTML_MODE_COMPACT)
                sberQr.movementMethod = LinkMovementMethod.getInstance()
                qrContainer.show()
            } else {
                qrContainer.gone()
            }

            qrHelp.setOnClickListener { Toast.makeText(itemView.context, R.string.help, Toast.LENGTH_LONG).show() }
        }
    }

}