package com.mospolytech.mospolyhelper.features.ui.schedule.ids

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemScheduleIdBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule

class ScheduleIdsAdapter(
    private val idList: List<UserSchedule>,
    private var query: String,
    private var filterMode: FilterModes,
    private val onItemClick: (UserSchedule) -> Unit
): RecyclerView.Adapter<ScheduleIdsAdapter.ViewHolder>() {

    private var filteredIdList: List<UserSchedule>

    init {
        filteredIdList = setFilteredList()
    }

    override fun getItemCount() = filteredIdList.size

    private fun setFilteredList(): List<UserSchedule> {
        return if (filterMode == FilterModes.All) {
            idList.filter { it.title.contains(query, ignoreCase = true) }
        } else {
            idList.filter {
                it.title.contains(query, ignoreCase = true) &&
                        it is StudentSchedule == (filterMode == FilterModes.Groups)}
        }
    }


    fun update(filterMode: FilterModes, query: String) {
        this.filterMode = filterMode
        this.query = query
        filteredIdList = setFilteredList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_schedule_id, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredIdList[position])
    }

    inner class ViewHolder(
        view: View
    ): RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemScheduleIdBinding::bind)

        init {
            viewBinding.textviewId.setOnClickListener { onItemClick(filteredIdList[bindingAdapterPosition]) }
        }

        fun bind(user: UserSchedule) {
            viewBinding.textviewId.text = user.title
            if (user is StudentSchedule) {
                viewBinding.textviewId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_fluent_people_24_regular, 0, 0, 0)
            } else {
                viewBinding.textviewId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_fluent_hat_graduation_24_regular, 0, 0, 0)
            }
        }
    }
}