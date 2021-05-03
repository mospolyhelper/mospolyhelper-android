package com.mospolytech.mospolyhelper.features.ui.schedule

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemScheduleDayBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.utils.ScheduleUtils.getOrderMap
import com.mospolytech.mospolyhelper.utils.TAG
import com.mospolytech.mospolyhelper.utils.WeakMutableSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class DayAdapter : RecyclerView.Adapter<DayAdapter.ViewHolder>() {

    private val activeViewHolders: MutableSet<ViewHolder> = WeakMutableSet()
    var dateRange = LocalDate.now()..LocalDate.now()
        private set
    private var selectedDay = LocalDate.MIN
    private var previousSelectedPosition = -1
    private var schedule: Schedule? = null

    fun update(from: LocalDate, to: LocalDate, schedule: Schedule?) {
        dateRange = from..to
        this.schedule = schedule
        notifyDataSetChanged()
    }

    fun updateSelectedDay(day: LocalDate): Int {
        selectedDay = day
        val oldPosition = previousSelectedPosition
        val position = dateRange.start.until(selectedDay, ChronoUnit.DAYS).toInt()
        previousSelectedPosition = position
        try {
            for (viewHolder in activeViewHolders) {
                when (viewHolder.bindingAdapterPosition) {
                    position -> {
                        val date = dateRange.start.plusDays(position.toLong())
                        viewHolder.updateIsSelected(date == selectedDay)
                    }
                    oldPosition -> {
                        if (oldPosition != -1) {
                            val date = dateRange.start.plusDays(oldPosition.toLong())
                            viewHolder.updateIsSelected(date == selectedDay)
                        }

                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "WeakReference exception", e)
        }
        return position
    }

    override fun getItemCount(): Int {
        return (dateRange.start.until(dateRange.endInclusive, ChronoUnit.DAYS) + 1).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_schedule_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        activeViewHolders.add(holder)
        val date = dateRange.start.plusDays(position.toLong())

        holder.bind(date, schedule?.getLessons(date).getOrderMap(), date == selectedDay, previousSelectedPosition)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        activeViewHolders.remove(holder)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            private val dateFormatter = DateTimeFormatter.ofPattern("EEE")
        }

        private val viewBinding by viewBinding(ItemScheduleDayBinding::bind)

        fun updateIsSelected(isSelected: Boolean) {
            if (isSelected) {
                viewBinding.root.backgroundTintList = ColorStateList.valueOf(
                    itemView.context.getColor(R.color.layerOneActivated)
                )
            } else {
                viewBinding.root.backgroundTintList = ColorStateList.valueOf(
                    itemView.context.getColor(R.color.layerOne)
                )
            }
        }

        fun bind(date: LocalDate, orderMap: Map<Int, Boolean>, isSelected: Boolean, previousSelectedPosition: Int) {
            viewBinding.textviewDayOfMonth.text = date.dayOfMonth.toString()
            viewBinding.textviewDayOfWeek.text = dateFormatter.format(date).capitalize()

            if (isSelected) {
                viewBinding.root.backgroundTintList = ColorStateList.valueOf(
                    itemView.context.getColor(R.color.layerOneActivated)
                )
            } else {
                viewBinding.root.backgroundTintList = ColorStateList.valueOf(
                    itemView.context.getColor(R.color.layerOne)
                )
            }


            for (i in 0 until viewBinding.linearlayoutOrderIndicators.childCount) {
                if (orderMap.getOrDefault(i, false)) {
                    viewBinding.linearlayoutOrderIndicators.getChildAt(i).visibility = View.VISIBLE
                } else {
                    viewBinding.linearlayoutOrderIndicators.getChildAt(i).visibility = View.GONE
                }
            }
        }
    }
}