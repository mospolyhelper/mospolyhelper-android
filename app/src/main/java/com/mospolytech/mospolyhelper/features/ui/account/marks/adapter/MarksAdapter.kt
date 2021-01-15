package com.mospolytech.mospolyhelper.features.ui.account.marks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark

class MarksAdapter(private var items : List<Mark>): RecyclerView.Adapter<ViewHolderMarks>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMarks {
        return ViewHolderMarks(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mark, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderMarks, position: Int) {
        holder.mark.text = items[position].mark
        holder.type.text = items[position].loadType
        holder.name.text = items[position].subject
    }

    override fun getItemCount(): Int {
        return items.size
    }
}