package com.mospolytech.mospolyhelper.features.ui.account.info.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemOrderBinding
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import kotlinx.android.synthetic.main.item_order.view.*
import java.util.*

class OrderAdapter(private var items : List<String>): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false))
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class OrderViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemOrderBinding::bind)

        val title: TextView = viewBinding.titleOrder
        val info: TextView = viewBinding.infoOrder
        val date: TextView = viewBinding.dateOrder

        fun bind(item: String) {
            title.text = item.subSequence(0, item.indexOf("от"))
            date.text = item.subSequence(item.indexOf("от") + 3, item.indexOf(" - «"))
            info.text = item.subSequence(item.indexOf("«") + 1, item.indexOf("»"))
        }
    }

}