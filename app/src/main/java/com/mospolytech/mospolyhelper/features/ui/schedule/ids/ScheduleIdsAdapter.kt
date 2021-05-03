package com.mospolytech.mospolyhelper.features.ui.schedule.ids

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
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
        holder.bind()
    }

    inner class ViewHolder(
        view: View
    ): RecyclerView.ViewHolder(view) {

        private val textView = view.findViewById<TextView>(R.id.textview_id)

        init {
            textView.setOnClickListener { onItemClick(filteredIdList[adapterPosition]) }
        }

        fun bind() {
            val item = filteredIdList[adapterPosition]
            textView.text = item.title
            if (item is StudentSchedule) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_id_group, 0, 0, 0)
            } else {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_id_teacher, 0, 0, 0)
            }
        }
    }
}