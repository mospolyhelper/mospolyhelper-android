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
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule

class ScheduleUsersAdapter :
    ListAdapter<UserSchedule, ScheduleUsersAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserSchedule>() {
            override fun areItemsTheSame(oldItem: UserSchedule, newItem: UserSchedule): Boolean =
                oldItem.idGlobal == newItem.idGlobal

            override fun areContentsTheSame(oldItem: UserSchedule, newItem: UserSchedule): Boolean =
                oldItem == newItem

        }
    }

    var onItemClickListener: (user: UserSchedule) -> Unit = { }

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

        var onClickListener: (user: UserSchedule) -> Unit = { }

        fun bind(user: UserSchedule) {
            viewBinding.root.setOnCloseIconClickListener {
                onClickListener(user)
            }
            viewBinding.root.text = user.title
        }
    }
}