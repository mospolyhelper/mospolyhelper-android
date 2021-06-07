package com.mospolytech.mospolyhelper.features.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.PageScheduleBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.features.ui.schedule.model.DailySchedulePack
import com.mospolytech.mospolyhelper.features.ui.schedule.model.SchedulePack
import com.mospolytech.mospolyhelper.features.utils.RecyclerViewInViewPagerHelper
import com.mospolytech.mospolyhelper.utils.WeakMutableSet
import java.time.LocalDate


class ScheduleAdapter(
    var schedulePack: SchedulePack,
    private var currentTimes: List<LessonTime>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_NULL = 0
        private const val VIEW_TYPE_NORMAL = 2
        private const val VIEW_TYPE_EMPTY = 3
    }
    private val commonPool = RecyclerView.RecycledViewPool()
    private val activeViewHolders: MutableSet<RecyclerView.ViewHolder> = WeakMutableSet()

    var lessonClick: (LessonTime, Lesson, LocalDate) -> Unit = { _, _, _ -> }


    fun submitData(schedulePack: SchedulePack) {
        this.schedulePack = schedulePack
    }

    fun setCurrentLessonTimes(currentTimes: List<LessonTime>) {
        this.currentTimes = currentTimes
        for (holder in activeViewHolders) {
            if (holder is ViewHolderDailySchedule) {
                holder.setCurrentLessonTimes(currentTimes)
            }
        }
    }

    override fun getItemCount() = if (schedulePack.isEmpty()) 1 else schedulePack.size

    override fun getItemViewType(position: Int): Int {
        return when {
            schedulePack.isEmpty() -> VIEW_TYPE_NULL
            schedulePack[position].lessons.isEmpty() -> VIEW_TYPE_EMPTY
            else -> VIEW_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NULL -> ViewHolderSimple(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule_null, parent, false)
            )
            VIEW_TYPE_EMPTY -> ViewHolderEmpty(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule_empty, parent, false)
            )
            VIEW_TYPE_NORMAL -> ViewHolderDailySchedule(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule, parent, false),
                commonPool,
                lessonClick
            )
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        activeViewHolders.add(holder)
        when (holder) {
            is ViewHolderDailySchedule -> {
                holder.bind(schedulePack[position], currentTimes)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        activeViewHolders.remove(holder)
    }

    class ViewHolderDailySchedule(
        view: View,
        recyclerViewPool: RecyclerView.RecycledViewPool,
        onItemClick: (LessonTime, Lesson, LocalDate) -> Unit = { _, _, _ -> }
    ) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(PageScheduleBinding::bind)
        private var dailySchedulePack: DailySchedulePack? = null

        init {
            with (viewBinding.recyclerviewLessons) {
                adapter = LessonAdapter().apply {
                    lessonClick = onItemClick
                }
                setRecycledViewPool(recyclerViewPool)
                layoutManager = LinearLayoutManager(view.context).apply {
                    recycleChildrenOnDetach = true
                }
                itemAnimator = null
                setHasFixedSize(true)
                addOnItemTouchListener(RecyclerViewInViewPagerHelper)
            }
        }

        fun bind(dailySchedule: DailySchedulePack, currentTimes: List<LessonTime>) {
            dailySchedulePack = dailySchedule
            val currentTimesFixed = if (dailySchedulePack != null && LocalDate.now() == dailySchedule.date) {
                currentTimes
            } else {
                emptyList()
            }
            viewBinding.recyclerviewLessons.scrollToPosition(0)
            (viewBinding.recyclerviewLessons.adapter as LessonAdapter).submitList(dailySchedule, currentTimesFixed)
        }

        fun setCurrentLessonTimes(currentTimes: List<LessonTime>) {
            val dailySchedulePack = dailySchedulePack
            if (dailySchedulePack != null && LocalDate.now() == dailySchedulePack.date) {
                (viewBinding.recyclerviewLessons.adapter as LessonAdapter)
                    .setCurrentLessonTimes(currentTimes)
            } else {
                (viewBinding.recyclerviewLessons.adapter as LessonAdapter)
                    .setCurrentLessonTimes(emptyList())
            }
        }
    }

    class ViewHolderSimple(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolderEmpty(view: View) : RecyclerView.ViewHolder(view)
}