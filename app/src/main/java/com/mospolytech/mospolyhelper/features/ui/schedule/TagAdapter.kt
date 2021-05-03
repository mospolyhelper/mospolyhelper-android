package com.mospolytech.mospolyhelper.features.ui.schedule

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag

class TagAdapter : RecyclerView.Adapter<TagViewHolder>() {

    var tags = emptyList<LessonTag>()

    override fun getItemCount() = tags.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tags[position])
    }
}

class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(tag: LessonTag) {
        (itemView as TextView).text = tag.title
        TextViewCompat.setCompoundDrawableTintList(itemView, ColorStateList.valueOf(tag.color))
    }
}