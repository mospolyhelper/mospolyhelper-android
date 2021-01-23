package com.mospolytech.mospolyhelper.features.ui.account.deadlines.adapter

import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import kotlinx.android.synthetic.main.item_deadline.view.*
import kotlinx.android.synthetic.main.item_deadlines.view.*

class DeadlinesViewHolder(view: View): RecyclerView.ViewHolder(view) {
    lateinit var deadline: Deadline
    val name: TextView = view.tvPred
    val description: TextView = view.tvZad
    val completed: RadioButton = view.deadline_completed
    val pinned: TextView = view.deadline_pinned
    val date: TextView = view.deadline_date


    fun bind(deadline: Deadline) {
        this.deadline = deadline
        name.text = deadline.name
        description.text = deadline.description
        completed.isChecked = deadline.completed
        pinned.isVisible = deadline.pinned
        date.text = deadline.date
    }
}