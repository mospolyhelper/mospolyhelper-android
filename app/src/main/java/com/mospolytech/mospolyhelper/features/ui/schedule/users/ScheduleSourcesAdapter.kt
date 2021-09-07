package com.mospolytech.mospolyhelper.features.ui.schedule.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemScheduleUserBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource

class ScheduleSourcesAdapter :
    ListAdapter<ScheduleSource, ScheduleSourcesAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ScheduleSource>() {
            override fun areItemsTheSame(oldItem: ScheduleSource, newItem: ScheduleSource): Boolean =
                oldItem.idGlobal == newItem.idGlobal

            override fun areContentsTheSame(oldItem: ScheduleSource, newItem: ScheduleSource): Boolean =
                oldItem == newItem

        }
    }

    var onItemClickListener: (source: ScheduleSource) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_schedule_user, parent, false)
        ).apply {
            onClickListener = onItemClickListener
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemScheduleUserBinding::bind)

        var onClickListener: (source: ScheduleSource) -> Unit = { }

        fun bind(source: ScheduleSource) {
            viewBinding.root.setOnCloseIconClickListener {
                onClickListener(source)
            }
            viewBinding.root.text = source.title
        }
    }
}