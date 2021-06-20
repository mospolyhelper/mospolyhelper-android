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
import com.mospolytech.mospolyhelper.features.ui.schedule.model.ScheduleDayUiData
import com.mospolytech.mospolyhelper.utils.TAG
import com.mospolytech.mospolyhelper.utils.WeakMutableSet
import com.mospolytech.mospolyhelper.utils.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DayAdapter : RecyclerView.Adapter<DayAdapter.ViewHolder>() {

    private val activeViewHolders: MutableSet<ViewHolder?> = WeakMutableSet()
    private var selectedPosition = -1
    private var previousSelectedPosition = -1
    var scheduleDays: List<ScheduleDayUiData> = emptyList()
        private set

    fun update(lessonsMap: List<ScheduleDayUiData>) {
        this.scheduleDays = lessonsMap
        notifyDataSetChanged()
    }

    fun updateSelectedDay(date: LocalDate): Int {
        selectedPosition = scheduleDays.indexOfFirst { it.date == date }
        if (selectedPosition == -1) return -1
        val oldPosition = previousSelectedPosition
        previousSelectedPosition = selectedPosition
        for (viewHolder in activeViewHolders) {
            viewHolder?.let {
                when (viewHolder.bindingAdapterPosition) {
                    selectedPosition -> {
                        viewHolder.updateIsSelected(true)
                    }
                    oldPosition -> {
                        if (oldPosition != -1) {
                            viewHolder.updateIsSelected(false)
                        }

                    }
                }
            }
        }
        return selectedPosition
    }

    override fun getItemCount() = scheduleDays.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_schedule_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        activeViewHolders.add(holder)
        val item = scheduleDays[position]
        holder.bind(item, position == selectedPosition)
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

        fun bind(day: ScheduleDayUiData, isSelected: Boolean) {
            viewBinding.textviewDayOfMonth.text = day.date.dayOfMonth.toString()
            viewBinding.textviewDayOfWeek.text = day.date.format(dateFormatter).capitalize()
            updateIsSelectedWithoutAnimation(isSelected)

            for (i in 0 until viewBinding.linearlayoutOrderIndicators.childCount) {
                if (day.orderMap.getOrDefault(i, false)) {
                    viewBinding.linearlayoutOrderIndicators.getChildAt(i).visibility = View.VISIBLE
                } else {
                    viewBinding.linearlayoutOrderIndicators.getChildAt(i).visibility = View.GONE
                }
            }
        }

        private fun updateIsSelectedWithoutAnimation(isSelected: Boolean) {
            this.isSelected = isSelected
            val colorTo: Int
            val scaleTo: Float
            val elevationTo: Float
            if (isSelected) {
                colorTo = itemView.context.getColor(R.color.layerOneActivated)
                scaleTo = 1.1f
                elevationTo = 2.dp(itemView.context)
            } else {
                colorTo = itemView.context.getColor(R.color.layerOne)
                scaleTo = 1.0f
                elevationTo = 0f
            }
            viewBinding.root.backgroundTintList = ColorStateList.valueOf(colorTo)
            viewBinding.root.scaleX = scaleTo
            viewBinding.root.scaleY = scaleTo
            viewBinding.root.translationZ = elevationTo
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
                    elevationFrom = 0f
                    elevationTo = 2.dp(itemView.context)
                } else {
                    colorFrom = itemView.context.getColor(R.color.layerOneActivated)
                    colorTo = itemView.context.getColor(R.color.layerOne)
                    scaleFrom = 1.1f
                    scaleTo = 1.0f
                    elevationFrom = 2.dp(itemView.context)
                    elevationTo = 0f
                }
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                    .apply {
                        duration = 200
                        addUpdateListener { animator ->
                            viewBinding.root.backgroundTintList =
                                ColorStateList.valueOf(animator.animatedValue as Int)
                        }
                    }

                val scaleAnimation = ValueAnimator.ofFloat(scaleFrom, scaleTo)
                    .apply {
                        duration = 200
                        addUpdateListener { animator ->
                            viewBinding.root.scaleX = animator.animatedValue as Float
                            viewBinding.root.scaleY = animator.animatedValue as Float
                        }
                    }

                val elevationAnimation = ValueAnimator.ofFloat(elevationFrom, elevationTo)
                    .apply {
                        duration = 200
                        addUpdateListener { animator ->
                            viewBinding.root.translationZ = animator.animatedValue as Float
                        }
                    }
                AnimatorSet().apply {
                    playTogether(colorAnimation, scaleAnimation, elevationAnimation)
                    start()
                }
            }
        }
    }
}