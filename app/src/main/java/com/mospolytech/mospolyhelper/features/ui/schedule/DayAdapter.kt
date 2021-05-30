package com.mospolytech.mospolyhelper.features.ui.schedule

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
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
import com.mospolytech.mospolyhelper.utils.dp
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

        private var isSelected: Boolean = false

        fun bind(date: LocalDate, orderMap: Map<Int, Boolean>, isSelected: Boolean, previousSelectedPosition: Int) {
            viewBinding.textviewDayOfMonth.text = date.dayOfMonth.toString()
            viewBinding.textviewDayOfWeek.text = dateFormatter.format(date).capitalize()
            updateIsSelected(isSelected)

            for (i in 0 until viewBinding.linearlayoutOrderIndicators.childCount) {
                if (orderMap.getOrDefault(i, false)) {
                    viewBinding.linearlayoutOrderIndicators.getChildAt(i).visibility = View.VISIBLE
                } else {
                    viewBinding.linearlayoutOrderIndicators.getChildAt(i).visibility = View.GONE
                }
            }
        }

        fun updateIsSelected(isSelected: Boolean) {
            val oldIsSelected = this.isSelected
            this.isSelected = isSelected
            if (oldIsSelected != isSelected) {
                val colorFrom: Int
                val colorTo: Int
                val scaleFrom: Float
                val scaleTo: Float
                val elevationFrom: Float
                val elevationTo: Float
                if (isSelected) {
                    colorFrom = itemView.context.getColor(R.color.layerOne)
                    colorTo = itemView.context.getColor(R.color.layerOneActivated)
                    scaleFrom = 1.0f
                    scaleTo = 1.1f
                    elevationFrom = 3.dp(itemView.context)
                    elevationTo = 5.dp(itemView.context)
                } else {
                    colorFrom = itemView.context.getColor(R.color.layerOneActivated)
                    colorTo = itemView.context.getColor(R.color.layerOne)
                    scaleFrom = 1.1f
                    scaleTo = 1.0f
                    elevationFrom = 5.dp(itemView.context)
                    elevationTo = 3.dp(itemView.context)
                }
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                colorAnimation.duration = 200
                colorAnimation.addUpdateListener { animator ->
                    viewBinding.root.backgroundTintList = ColorStateList.valueOf(animator.animatedValue as Int)
                }

                val scaleAnimation = ValueAnimator.ofFloat(scaleFrom, scaleTo)
                scaleAnimation.duration = 200
                scaleAnimation.addUpdateListener { animator ->
                    viewBinding.root.scaleX = animator.animatedValue as Float
                    viewBinding.root.scaleY = animator.animatedValue as Float
                }

                val elevationAnimation = ValueAnimator.ofFloat(elevationFrom, elevationTo)
                scaleAnimation.duration = 200
                scaleAnimation.addUpdateListener { animator ->
                    viewBinding.root.elevation = animator.animatedValue as Float
                    viewBinding.root.elevation = animator.animatedValue as Float
                }
                AnimatorSet().apply {
                    playTogether(colorAnimation, scaleAnimation, elevationAnimation)
                    start()
                }
            }
        }
    }
}