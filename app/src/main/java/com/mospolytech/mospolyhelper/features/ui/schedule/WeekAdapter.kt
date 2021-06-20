package com.mospolytech.mospolyhelper.features.ui.schedule

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.PageScheduleDateBinding
import com.mospolytech.mospolyhelper.features.ui.schedule.model.ScheduleWeekUiData
import com.mospolytech.mospolyhelper.features.utils.CustomLayoutManager
import com.mospolytech.mospolyhelper.utils.TAG
import com.mospolytech.mospolyhelper.utils.WeakMutableSet
import java.time.LocalDate


class WeekAdapter : RecyclerView.Adapter<WeekAdapter.ViewHolder>() {
    private val activeViewHolders: MutableSet<ViewHolder?> = WeakMutableSet()
    private var lessonMap: List<ScheduleWeekUiData> = emptyList()
    private var selectedDay = LocalDate.MIN

    fun update(lessonMap: List<ScheduleWeekUiData>, selectedDate: LocalDate) {
        this.lessonMap = lessonMap
        selectedDay = selectedDate
        notifyDataSetChanged()
    }

    fun updateSelectedDay(day: LocalDate) {
        selectedDay = day
        for (viewHolder in activeViewHolders) {
            viewHolder?.updateSelectedDay(day)
        }
    }

    override fun getItemCount(): Int = lessonMap.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.page_schedule_date, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        activeViewHolders.add(holder)
        holder.bind(lessonMap[position], selectedDay)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        activeViewHolders.remove(holder)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(PageScheduleDateBinding::bind)

        init {
            viewBinding.recyclerviewDays.setHasFixedSize(true)
            viewBinding.recyclerviewDays.layoutManager = CustomLayoutManager(viewBinding.root.context, LinearLayoutManager.HORIZONTAL)
            viewBinding.recyclerviewDays.isNestedScrollingEnabled = false
            viewBinding.recyclerviewDays.adapter = DayAdapter()
        }

        fun updateSelectedDay(date: LocalDate) {
            (viewBinding.recyclerviewDays.adapter as? DayAdapter)?.let { dayAdapter ->
                if (!dayAdapter.scheduleDays.any { it.date == date }) {
                    return
                }
                val position = dayAdapter.updateSelectedDay(date)
                viewBinding.recyclerviewDays.smoothScrollToPosition(position)
            }
        }

        fun bind(lessonsWeekMap: ScheduleWeekUiData, selectedDate: LocalDate) {
            with(viewBinding.recyclerviewDays.adapter as DayAdapter) {
                update(lessonsWeekMap)
                if (!scheduleDays.any { it.date == selectedDate }) {
                    return
                }
                val position = this.updateSelectedDay(selectedDate)
                viewBinding.recyclerviewDays.post {
                    viewBinding.recyclerviewDays.smoothScrollToPosition(position)
                }
            }
        }
    }
}