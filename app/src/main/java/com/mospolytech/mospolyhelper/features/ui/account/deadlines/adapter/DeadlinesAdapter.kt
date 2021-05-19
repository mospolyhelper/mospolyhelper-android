package com.mospolytech.mospolyhelper.features.ui.account.deadlines.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemDeadlinesBinding
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.utils.inflate

class DeadlinesAdapter(private var items: List<Deadline>):RecyclerView.Adapter<DeadlinesAdapter.DeadlinesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeadlinesViewHolder {
        return DeadlinesViewHolder(parent.inflate(R.layout.item_deadlines))
    }

    override fun onBindViewHolder(holder: DeadlinesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    class DeadlinesViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemDeadlinesBinding::bind)

        lateinit var deadline: Deadline
        val name: TextView = viewBinding.deadlineName
        val description: TextView = viewBinding.deadlineDescription
        val completed: RadioButton = viewBinding.deadlineCompleted
        val pinned: TextView = viewBinding.deadlinePinned
        val date: TextView = viewBinding.deadlineDate


        fun bind(deadline: Deadline) {
            this.deadline = deadline
            name.text = deadline.name
            description.text = deadline.description
            completed.isChecked = deadline.completed
            pinned.isVisible = deadline.pinned
            date.text = deadline.date
        }
    }

}