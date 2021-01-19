package com.mospolytech.mospolyhelper.features.ui.account.info.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import java.util.*

class OrderAdapter(private var items : List<String>): RecyclerView.Adapter<OrderViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        context = parent.context
        return OrderViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false))
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.title.text = items[position].subSequence(0, items[position].indexOf("от"))
        holder.date.text = items[position].subSequence(items[position].indexOf("от") + 3, items[position].indexOf(" - «"))
        holder.info.text = items[position].subSequence(items[position].indexOf("«") + 1, items[position].indexOf("»"))
    }

    override fun getItemCount(): Int {
        return items.size
    }

}