package com.mospolytech.mospolyhelper.features.ui.schedule.ids

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R

class ScheduleIdsAdapter(
    private val idList: List<Pair<Boolean, String>>,
    private var query: String,
    private var filterMode: FilterModes,
    private val onItemClick: (Pair<Boolean, String>) -> Unit
): RecyclerView.Adapter<ScheduleIdsAdapter.ViewHolder>() {

    private var filteredIdList: List<Pair<Boolean, String>>

    init {
        filteredIdList = setFilteredList()
    }

    override fun getItemCount() = filteredIdList.size

    private fun setFilteredList(): List<Pair<Boolean, String>> {
        return if (filterMode == FilterModes.All) {
            idList.filter { it.second.contains(query, ignoreCase = true) }
        } else {
            idList.filter {
                it.second.contains(query, ignoreCase = true) &&
                        it.first == (filterMode == FilterModes.Groups)}
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
            textView.text = item.second
            if (item.first) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_id_group, 0, 0, 0)
            } else {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_id_teacher, 0, 0, 0)
            }
        }
    }
}