package com.mospolytech.mospolyhelper.features.ui.schedule

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.PageScheduleBinding
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.CurrentLesson
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonPlace
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleItem
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.utils.ScheduleUtils
import com.mospolytech.mospolyhelper.utils.Action2
import com.mospolytech.mospolyhelper.utils.Event2
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class ScheduleAdapter(
    val schedule: Schedule?,
    private val tags: List<LessonTag>,
    private val deadlines: Map<String, List<Deadline>>,
    private val showEndedLessons: Boolean,
    private val showCurrentLessons: Boolean,
    private val showNotStartedLessons: Boolean,
    private val showEmptyLessons: Boolean,
    private val showGroups: Boolean,
    private val showTeachers: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val MAX_COUNT = 400
        private const val VIEW_TYPE_NULL = 0
        private const val VIEW_TYPE_NORMAL = 2
        private const val VIEW_TYPE_EMPTY = 3
        private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
        private val dateFormatterWeek = DateTimeFormatter.ofPattern("EEEE")
    }
    var firstPosDate: LocalDate = LocalDate.now()
    private var count = 0
    private val commonPool = RecyclerView.RecycledViewPool()

    val lessonClick: Event2<LessonPlace, LocalDate> = Action2()
    private val timerTick: Event2<List<CurrentLesson>, Boolean> = Action2()

    init {
        setCount()
        setFirstPosDate()
    }
    var prevCurrentLesson = mutableListOf<CurrentLesson>()
//    fun updateCurrentLesson(currentLessons: List<CurrentLesson>) {
//        timerTick as Action2
//        val updatePrev = prevCurrentLesson.any { it.order != }prevCurrentLesson.first.order != currentLessons.first.order ||
//                prevCurrentLesson.second.order != currentLessons.second.order
//        prevCurrentLesson = currentLessons
//        timerTick.invoke(currentLessons, updatePrev)
//    }

    override fun getItemCount() = count


    private fun setCount() {
        if (schedule == null) {
            count = 1
        } else {
            count = schedule.dateFrom.until(schedule.dateTo, ChronoUnit.DAYS).toInt() + 1
            if (count  !in 1..MAX_COUNT) {
                count = MAX_COUNT
            }
        }
    }

    private fun setFirstPosDate() {
        if (schedule != null) {
            firstPosDate = if (count == MAX_COUNT) {
                LocalDate.now().minusDays((MAX_COUNT / 2).toLong())
            } else {
                schedule.dateFrom
            }
        }
    }

    override fun getItemViewType(position: Int) = when {
        schedule == null -> VIEW_TYPE_NULL
        schedule
            .getLessons(
                firstPosDate.plusDays(position.toLong()),
                showEndedLessons,
                showCurrentLessons,
                showNotStartedLessons
            ).isEmpty() -> {
            VIEW_TYPE_EMPTY
        }
        else -> {
            VIEW_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_NULL -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule_null, parent, false)
                return ViewHolderSimple(view)
            }
            VIEW_TYPE_EMPTY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule_empty, parent, false)
                return ViewHolderEmpty(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule, parent, false)
                return ViewHolder(view, commonPool, (lessonClick as Action2)::invoke)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            VIEW_TYPE_NORMAL -> {
                val date = firstPosDate.plusDays(position.toLong())

                var rawDailySchedule = schedule!!.getLessons(
                    date,
                    showEndedLessons,
                    showCurrentLessons,
                    showNotStartedLessons
                )
                if (showEmptyLessons) rawDailySchedule = ScheduleUtils.getEmptyPairsDecorator(rawDailySchedule)
                val dailySchedule = ScheduleUtils.getWindowsDecorator(rawDailySchedule)

                (viewHolder as ViewHolder).bind(SchedulePack(dailySchedule, date, showGroups, showTeachers))
            }
            VIEW_TYPE_EMPTY -> (viewHolder as ViewHolderEmpty).bind()
        }
    }

    inner class ViewHolderSimple(val view: View) : RecyclerView.ViewHolder(view)

    inner class ViewHolderEmpty(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
        }
    }


    class ViewHolder(
        view: View,
        recyclerViewPool: RecyclerView.RecycledViewPool,
        onItemClick: (LessonPlace, LocalDate) -> Unit = { _, _ -> }
    ) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(PageScheduleBinding::bind)
        private val listAdapter = LessonAdapter()

        init {
            listAdapter.lessonClick += onItemClick
            viewBinding.recyclerviewLessons.adapter = listAdapter

            viewBinding.recyclerviewLessons.setRecycledViewPool(recyclerViewPool)
            viewBinding.recyclerviewLessons.layoutManager = LinearLayoutManager(view.context).apply {
                recycleChildrenOnDetach = true
            }
            viewBinding.recyclerviewLessons.itemAnimator = null
            viewBinding.recyclerviewLessons.setHasFixedSize(true)

            // To solve viewpager - recyclerview conflict when you try to scroll vertically
            viewBinding.recyclerviewLessons.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    if (e.action == MotionEvent.ACTION_DOWN &&
                        rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING
                    ) {
                        rv.stopScroll()
                    }
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
            })
        }


        fun bind(item: SchedulePack) {
            viewBinding.recyclerviewLessons.scrollToPosition(0)
            listAdapter.submitList(
                item.dailySchedule,
                item.date,
                item.showGroups,
                item.showTeachers
            )
        }
    }

    class SchedulePack(
        val dailySchedule: List<ScheduleItem>,
        val date: LocalDate,
        val showGroups: Boolean,
        val showTeachers: Boolean
    )
}