package com.mospolytech.mospolyhelper.features.ui.schedule

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.PageScheduleDateBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.utils.TAG
import com.mospolytech.mospolyhelper.utils.WeakMutableSet
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class DateAdapter : RecyclerView.Adapter<DateAdapter.ViewHolder>() {
    companion object {
        fun getMondayOfWeek(date: LocalDate): LocalDate {
            return date.plusDays(-date.dayOfWeek.value + 1L)
        }
    }

    private val activeViewHolders: MutableSet<ViewHolder> = WeakMutableSet()
    private var dateRange = LocalDate.now()..LocalDate.now()
    private var schedule: Schedule? = null
    private var selectedDay = LocalDate.MIN

    fun update(from: LocalDate, to: LocalDate, selectedDate: LocalDate, schedule: Schedule?) {
        dateRange = getMondayOfWeek(from)..getMondayOfWeek(to)
        this.schedule = schedule
        selectedDay = selectedDate
        notifyDataSetChanged()
    }

    fun updateSelectedDay(day: LocalDate) {
        try {
            for (viewHolder in activeViewHolders) {
                viewHolder.updateSelectedDay(day)
            }
        } catch (e: Exception) {
            Log.e(TAG, "WeakReference exception", e)
        }
    }

    fun getPositionFromDate(date: LocalDate): Int {
        val monday = getMondayOfWeek(date)
        return (dateRange.start.until(monday, ChronoUnit.DAYS) / 7L).toInt()
    }

    override fun getItemCount(): Int {
        return (dateRange.start.until(dateRange.endInclusive, ChronoUnit.DAYS) / 7L + 1).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.page_schedule_date, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        activeViewHolders.add(holder)
        holder.bind(dateRange.start.plusDays(7L * position), selectedDay, schedule)
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

        class CustomLayoutManager(private val context: Context, layoutDirection: Int):
            LinearLayoutManager(context, layoutDirection, false) {

            companion object {
                // This determines how smooth the scrolling will be
                private
                const val MILLISECONDS_PER_INCH = 300f
            }

            override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {

                val smoothScroller: LinearSmoothScroller = object: LinearSmoothScroller(context) {

                    fun dp2px(dpValue: Float): Int {
                        val scale = context.resources.displayMetrics.density
                        return (dpValue * scale + 0.5f).toInt()
                    }

                    // change this and the return super type to "calculateDyToMakeVisible" if the layout direction is set to VERTICAL
                    override fun calculateDxToMakeVisible(view: View ? , snapPreference : Int): Int {
                        return super.calculateDxToMakeVisible(view, SNAP_TO_END) - dp2px(50f)
                    }

                    //This controls the direction in which smoothScroll looks for your view
                    override fun computeScrollVectorForPosition(targetPosition: Int): PointF ? {
                        return this@CustomLayoutManager.computeScrollVectorForPosition(targetPosition)
                    }

                    //This returns the milliseconds it takes to scroll one pixel.
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                    }
                }
                smoothScroller.targetPosition = position
                startSmoothScroll(smoothScroller)
            }
        }

        fun updateSelectedDay(date: LocalDate) {
            val dayAdapter = viewBinding.recyclerviewDays.adapter

            if (dayAdapter is DayAdapter) {
                if (date !in dayAdapter.dateRange) {
                    return
                }
                val position = dayAdapter.updateSelectedDay(date)

                //viewBinding.recyclerviewDays.smoothSnapToPosition(position)
                viewBinding.recyclerviewDays.smoothScrollToPosition(position)
            }
        }

        fun bind(monday: LocalDate, selectedDate: LocalDate, schedule: Schedule?) {
            val sunday = monday.plusDays(6)

            with(viewBinding.recyclerviewDays.adapter as DayAdapter) {
                update(monday, sunday, schedule)
                if (selectedDate !in this.dateRange) {
                    return
                }
                val position = updateSelectedDay(selectedDate)
                viewBinding.recyclerviewDays.post {
                    viewBinding.recyclerviewDays.smoothScrollToPosition(position)//scrollToPosition(position)
                }
            }
        }
    }
}