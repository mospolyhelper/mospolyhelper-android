package com.mospolytech.mospolyhelper.features.ui.account.applications.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import java.util.*

class ApplicationsAdapter(private var items: List<Application>): RecyclerView.Adapter<ApplicationsViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationsViewHolder {
        context = parent.context
        return ApplicationsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_application, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ApplicationsViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            items[position].isShown = !items[position].isShown
            notifyItemChanged(position)
        }
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}