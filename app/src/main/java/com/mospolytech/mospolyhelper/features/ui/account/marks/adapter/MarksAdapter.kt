package com.mospolytech.mospolyhelper.features.ui.account.marks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import java.util.*

class MarksAdapter(private var items : List<Mark>): RecyclerView.Adapter<ViewHolderMarks>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMarks {
        context = parent.context
        return ViewHolderMarks(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mark, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderMarks, position: Int) {
        holder.type.text = items[position].loadType
        holder.name.text = items[position].subject
        var mark = ""
        when (items[position].mark.toLowerCase(Locale.getDefault())) {
            "отлично" -> {
                mark ="5"
                holder.progress.progress = -100f
                holder.progress.foregroundStrokeColor = ContextCompat.getColor(context, R.color.colorLow)
            }
            "хорошо" -> {
                mark ="4"
                holder.progress.progress = -75f
                holder.progress.foregroundStrokeColor = ContextCompat.getColor(context, R.color.colorLow)
            }
            "удовлетворительно" -> {
                mark ="3"
                holder.progress.progress = -50f
                holder.progress.foregroundStrokeColor = ContextCompat.getColor(context, R.color.colorMedium)
            }
            "неудовлетворительно" -> {
                mark ="2"
                holder.progress.progress = -25f
                holder.progress.foregroundStrokeColor = ContextCompat.getColor(context, R.color.colorHigh)
            }
            "не явился" -> {
                mark ="2"
                holder.progress.progress = -25f
                holder.progress.foregroundStrokeColor = ContextCompat.getColor(context, R.color.colorHigh)
            }
            "зачтено" -> {
                mark ="Зач"
                holder.progress.progress = -100f
                holder.progress.foregroundStrokeColor = ContextCompat.getColor(context, R.color.colorLow)
            }
            "незачтено" -> {
                mark ="Нез"
                holder.progress.progress = -25f
                holder.progress.foregroundStrokeColor = ContextCompat.getColor(context, R.color.colorHigh)
            }
            "не зачтено" -> {
                mark ="Нез"
                holder.progress.progress = -25f
                holder.progress.foregroundStrokeColor = ContextCompat.getColor(context, R.color.colorHigh)
            }
            else -> items[position].mark.substring(0, 2)
        }
        holder.mark.text = mark
    }

    override fun getItemCount(): Int {
        return items.size
    }

}