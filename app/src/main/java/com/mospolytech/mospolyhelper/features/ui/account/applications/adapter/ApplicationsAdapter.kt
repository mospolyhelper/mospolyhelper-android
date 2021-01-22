package com.mospolytech.mospolyhelper.features.ui.account.applications.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import java.util.*

class ApplicationsAdapter(private var items : List<Application>): RecyclerView.Adapter<ApplicationsViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationsViewHolder {
        context = parent.context
        return ApplicationsViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application, parent, false))
    }

    override fun onBindViewHolder(holder: ApplicationsViewHolder, position: Int) {
        holder.title.text = items[position].name
        holder.date.text = items[position].dateTime
        holder.info.text = items[position].status
            .subSequence(0, items[position].status
                .indexOf("<br><br>", 0, true)).trim()
        when (holder.info.text) {
            "Получено" -> holder.info.setTextColor(ContextCompat.getColor(context, R.color.colorLow))
            "Отклонено" -> holder.info.setTextColor(ContextCompat.getColor(context, R.color.colorHigh))
            "Готово" -> holder.info.setTextColor(ContextCompat.getColor(context, R.color.predmetcolor))
            else -> holder.info.setTextColor(ContextCompat.getColor(context, R.color.accent_text_color))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}