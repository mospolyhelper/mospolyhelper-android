package com.mospolytech.mospolyhelper.features.ui.account.deadlines.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.utils.inflate

class DeadlinesAdapter(private var items: List<Deadline>):RecyclerView.Adapter<DeadlinesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeadlinesViewHolder {
        return DeadlinesViewHolder(parent.inflate(R.layout.item_deadlines))
    }

    override fun onBindViewHolder(holder: DeadlinesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }
}